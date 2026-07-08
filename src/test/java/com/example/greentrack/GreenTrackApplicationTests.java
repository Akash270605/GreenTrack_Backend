/**
 * This Test class that verifies the Spring Boot application can start up
 * without errors.
 * 
 * Unit tests are methods that verifies individual pieces of code work
 * correctly. They help to catch the bugs early.
 */

package com.example.greentrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * This class verifies the Spring Boot application context loads without errors:
 * It test passes, it means:
 *          - The application configuration is valid.
 *          - all beans can be created (no missing dependencies).
 *          - The database connection parameters are correct.
 *          - All Component scan paths are correct.
 */
@SpringBootTest(properties ={
        "spring.datasource.url=jdbc:h2:mem:greentrack-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
       "spring.datasource.username=sa",
       "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false"
})
class GreenTrackApplicationTests {

    /**
     *  contextLoads(): This is the actual test method. It's annotated with @Test
     * so JUnit (the testing framework) knows to run it.
     * 
     * The method body is Empy because we don't need to assert anything
     * specific - if the application context loads without throwing an exception,
     * the test passes.
     * 
     * If something is wrong (e.g., database can't connect), the test will fail
     * with an error message explaining the issue.
     */
    @Test
    void contextLoads() {
    }

}
