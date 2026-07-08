/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This Service handles importing emission records from uploaded files
 * (CSV or Excel .xlsx/.xls format).
 * 
 * The import process:
 *      1. Read all rows from the file.
 *      2. For each row, try to create an EmissionRecord.
 *      3. If successful, count it as "imported".
 *      4. If an error occurs (e.g., missing field), record the error with the row no.
 *          but Continue processing the rest of the rows (no partial failure).
 *      5. Return a summary: how many imported, how many rejected, and the 
 *          list of errors.
 */
package com.example.greentrack.emissions;

import com.example.greentrack.auth.User;
import com.example.greentrack.emissions.dto.EmissionRecordRequest;
import com.example.greentrack.emissions.dto.UploadError;
import com.example.greentrack.emissions.dto.UploadResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 * EmissionImportService: Parses CSV and Excel files containing emission data
 *                                  and creates emission records from each valid row.
 * 
 * @Transactional       -> The entire import runs in one transaction
 *                                      (all-or-nothing for the whole file).
 */
@Service
public class EmissionImportService {
    
    private final EmissionRecordService recordService;
    
    public EmissionImportService(EmissionRecordService recordService){
        this.recordService = recordService;
    }
    
    /**
     * importFile(): Main entry point. Reads the uploaded file, parses it, creates
     *                      records, and returns a summary.
     * 
     * Throws IllegalArgumentException if file is empty.
     */
    @Transactional
    public UploadResponse importFile(User user, MultipartFile file){
        
        // Check if file is empty
        if(file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        
        // Read all rows from the file as a list of key-value maps.
        List<Map<String, String>> rows = readRows(file);
        
        List<UploadError> errors = new ArrayList<>();
        int imported = 0;
        
        // Process each row. Row number = i + 2 (1-indexed + header row).
        for(int i=0; i<rows.size(); i++){
            int rowNumber = i+2;
            try{
                // Convert the map to an EmissionRecordRequest and save it.
                EmissionRecordRequest request = toRequest(rows.get(i));
                recordService.create(user, request);
                imported++;
            }catch (RuntimeException ex){
                // Catch any error (validation, missing fields, etc.)
                // and record it without stopping the import
                errors.add(new UploadError(rowNumber, ex.getMessage()));
            }
        }
        
        return new UploadResponse(imported, errors.size(), errors);
    }
    
    /**
     * readRows(): Determine the file type and delegates to the appropriate parser
     *                  (CSV or Excel).
     * Returns a list of rows, each represented as a Map<String, String> where
     * keys are column headers and values are cell values.
     */
    private List<Map<String, String>> readRows(MultipartFile file){
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        try{
            if(filename.endsWith(".xlsx") || filename.endsWith(".xls")){
                return readExcelRows(file);
            }
            return readCsvRows(file);
        }catch (Exception ex){
            throw new IllegalArgumentException("Could not parse upload file: "+ ex.getMessage());
        }
    }
    
    /**
     * readCsvRows(): Parses a CSV file using Apache Commons CSV.
     * Returns List of rows as key-value maps.
     * Throws Exception if parsing fails.
     */
    private List<Map<String, String>> readCsvRows(MultipartFile file) throws Exception{
        try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()              // First row = column names
                     .setSkipHeaderRecord(true) // Skip header row when iterating
                     .setTrim(true)             // Trim whitespace from values
                     .build()
                     .parse(reader)){
            
            List<Map<String, String>> rows = new ArrayList<>();
            for(CSVRecord record : parser){
                rows.add(record.toMap());       // Convert each CSV row to a map
            }
            return rows;
        }
    }
    
    /**
     * readExcelRows(): Parses an Excel (.xlsx/.xls) file using Apache POI.
     * Returns list of rows as key-value maps.
     * Throws Exception if parsing fails.
     */
    private List<Map<String, String>> readExcelRows(MultipartFile file) throws Exception{
        List<Map<String, String>> rows = new ArrayList<>();
        
        // WorkbookFactory automatically detects .xlsx vs .xls
        try(var workbook = WorkbookFactory.create(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);       // Read first sheet only
            
            // Row 0 = header row -> extract column names.
            Row headerRow = sheet.getRow(0);
            if(headerRow == null){
                return rows;        // Empty file, no headers.
            }
            
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(cell.getStringCellValue()));
            
            // Rows 1+ = data rows.
            for(int i=1; i<= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) continue;       // Skip empty rows.
                
                Map<String, String> values = new HashMap<>();
                for(int c = 0; c<headers.size(); c++){
                    values.put(headers.get(c), readCell(row.getCell(c)));
                }
                rows.add(values);
            }
        }
        return rows;
    }
    
    /**
     * readCell(): Reads a single Excel cell and converts its value to a string.
     * Returns the cell value as a string, or "" if null.
     * 
     * cell : The Excel cell (may be null).
     */
    private String readCell(Cell cell){
        if(cell == null){
            return "";
        }
        
        return switch (cell.getCellType()){
            case STRING         -> cell.getStringCellValue();
            case NUMERIC      -> DateUtil.isCellDateFormatted(cell) 
                ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                : BigDecimal.valueOf(cell.getNumericCellValue())
                            .stripTrailingZeros().toPlainString();
            case BOOLEAN     -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA     -> cell.getCellFormula();
            default                    -> "";
        };
    }
    
    /**
     *  toRequest(): Converts a row map (column name -> value) int an
     *              EmissionRecordRequest DTO.
     * 
     * Throws IllegalArgumentException if a required column is missing.
     */
    private EmissionRecordRequest toRequest(Map<String, String> row){
        if(isCompanyEsgFinancialRow(row)){
            return toCompanyEsgFinancialRequest(row);
        }
        
        return new EmissionRecordRequest(
                required(row, "facility"),
                required(row, "businessUnit"),
                parseScope(required(row, "scope")),
                required(row, "activityType"),
                LocalDate.parse(required(row, "activityDate")),
                new BigDecimal(required(row, "quantity")),
                required(row, "unit"),
                blankToNull(value(row, "emissionFactorKey")),
                parseOptionalDecimal(value(row, "emissionFactor")),
                blankToNull(value(row, "sourceDescription"))
        );
    }
    
    // Checks whether the required fields found or not in CSV/Excel file
    private boolean isCompanyEsgFinancialRow(Map<String, String> row){
        Set<String> headers = row.keySet();
        return containsIgnoreCase(headers, "CompanyName")
                && containsIgnoreCase(headers, "Industry")
                && containsIgnoreCase(headers, "Year")
                && containsIgnoreCase(headers, "CarbonEmissions");
    }
    
    // Convert to EmissionRecordRequest from the given CSV/Excel file.
    private EmissionRecordRequest toCompanyEsgFinancialRequest(Map<String, String> row){
        String year = required(row, "Year");
        return new EmissionRecordRequest(
                required(row, "CompanyName"),
                required(row, "Industry"),
                EmissionScope.SCOPE_1,
                "Company carbon emissions",
                LocalDate.of(Integer.parseInt(year), 12, 31),
                new BigDecimal(required(row, "CarbonEmissions")),
                "kgCO2e",
                null,
                BigDecimal.ONE,
                truncate(sourceDescription(row), 255)
        );
    }
    
    
    // Create source description for each row of the given CSV/Excel file
    private String sourceDescription(Map<String, String> row){
        return "Dataset row"
                + ", CompanyId=" + optional(row, "CompanyID")
                + ", Region=" + optional(row, "Region")
                + ", Year=" + optional(row, "Year")
                + ", ESG=" +optional(row, "ESG_Overall")
                + ", E=" + optional(row, "ESG_Environmental")
                + ", S=" + optional(row, "ESG_Social")
                + ", G=" + optional(row, "ESG_Governance")
                + ", Revenue="+ optional(row, "Revenue")
                + ", ProfitMargin=" + optional(row, "ProfitMargin")
                + ", GrowthRate=" + optional(row, "GrowthRate")
                + ", WaterUsage=" + optional(row, "WaterUsage")
                + ", EnergyConsumption="+ optional(row, "EnergyConscumption");
    }
    
    
    // ------- Helper Methods ------------
    
    /**
     * required(): Gets a value from the row map and throws if it's null/blank.
     * 
     * row -> The row map.
     * key -> The column name.
     * 
     * Returns the trimmed value.
     * Throws IllegalArgumentException if the column is missing.
     */
    private String required(Map<String, String> row, String key){
        String value = blankToNull(value(row, key));
        if(value == null){
            throw new IllegalArgumentException("Missing required column: " + key);
        }
        return value;
    }
    
    
    // Get the optional values
    private String optional(Map<String, String> row, String key){
        String value = blankToNull(value(row, key));
        return value == null ? "" : value;
    }
    
    // Get the value
    private String value(Map<String, String> row, String key){
        String exact = row.get(key);
        if(exact != null){
            return exact;
        }
        
        return row.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
    
    // Check whether the string is found, ignoring the case
    private boolean containsIgnoreCase(Set<String> values, String expected){
        return values.stream().anyMatch(value -> value.equalsIgnoreCase(expected));
    }
    
    // truncate the string if its length exceeds the maxlength
    private String truncate(String value, int maxLength){
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
    
    /**
     * parseScope(): Converts a scope value from the file into an EmissionScope enum.
     * Accepts various formats: "SCOPE_1", "1", "scope1", "Scope 1", etc.
     * 
     * value -> The scope string from the file.
     * Returns the corresponding EmissionScope enum value.
     */
    private EmissionScope parseScope(String value){
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        if(normalized.equals("1") || normalized.equals("SCOPE1")){
            return EmissionScope.SCOPE_1;
        }
        
        if(normalized.equals("2") || normalized.equals("SCOPE2")){
            return EmissionScope.SCOPE_2;
        }
        
        if(normalized.equals("3") || normalized.equals("SCOPE3")){
            return EmissionScope.SCOPE_3;
        }
        
        return EmissionScope.valueOf(normalized);
    }
    
    /**
     * parseOptionalDecimal(): Parses a string to BigDecimal, or returns null if  blank.
     * 
     * value -> The string value.
     */
    private BigDecimal parseOptionalDecimal(String value){
        String cleaned = blankToNull(value);
        return cleaned == null ? null : new BigDecimal(cleaned);
    }
    
    /**
     * blankToNull(): Converts null or blank strings to null (trimmed).
     * Return null if blank/empty, otherwise the trimmed value.
     */
    private String blankToNull(String value){
        return value == null || value.isBlank() ? null : value.trim();
    }
}
