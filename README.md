# GreenTrack

Spring Boot backend for GreenTrack, an enterprise sustainability intelligence platform.

## Features

- JWT registration and login with BCrypt password hashing.
- Role-based access control for `ADMIN`, `ESG_MANAGER`, `ANALYST`, and `VIEWER`.
- CSV and Excel emission data import with row-level validation feedback.
- Emission CRUD APIs and Scope 1, Scope 2, and Scope 3 calculation support.
- Configurable emission factors with tenant override and default fallback.
- Dashboard metrics, scope totals, monthly/yearly trends, and year-over-year comparison.
- Swagger/OpenAPI documentation.
- MySQL persistence with Flyway migrations.

## Run Locally

Create or start MySQL, then set credentials if they differ from the defaults:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/greentrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:JWT_SECRET="replace-with-a-base64-encoded-256-bit-secret"
mvn spring-boot:run
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Import Columns

CSV/XLSX uploads use the first row as headers:

```text
facility,businessUnit,scope,activityType,activityDate,quantity,unit,emissionFactorKey,emissionFactor,sourceDescription
```

Provide either `emissionFactorKey` or `emissionFactor`. Supported scope values include `SCOPE_1`, `SCOPE_2`, `SCOPE_3`, `1`, `2`, and `3`.

The sample ESG financial dataset is also supported:

```text
CompanyID,CompanyName,Industry,Region,Year,Revenue,ProfitMargin,MarketCap,GrowthRate,ESG_Overall,ESG_Environmental,ESG_Social,ESG_Governance,CarbonEmissions,WaterUsage,EnergyConsumption
```

For this dataset, each row is imported as a company carbon-emission record. `CompanyName` maps to facility, `Industry` maps to business unit, `Year` maps to December 31 of that year, and `CarbonEmissions` is stored directly as `kgCO2e` using an emission factor of `1`.
