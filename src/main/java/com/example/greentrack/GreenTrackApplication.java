package com.example.greentrack;


/**
 *  SpringApplication: A utility class provided by Spring Boot to bootstrap (start)
 *                                          the application.
 * @SpringBootApplication: This annotation tells Spring Boot that this is 
 *                                           the main configuration class.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * How Spring Boot Works: 
 *      - @SpringBootApplication is convenient annotation that combines:
 *              1. @Configuration       -> Marks this class as a source of bean definitions.
 *              2. @EnableAutoConfiguration -> Tells Spring Boot to automatically
 *                              configure components (e.g. database, web server) based on 
 *                              the libraries if finds in the classpath.
 *              3. @ComponentScan   -> Tells Spring to look for other classes with
 *                              annotations like @Controller, @Service, @Repository, etc.
 * 
 *      - The main() method calls SpringApplication.run() which starts everything up.
 */
@SpringBootApplication
public class GreenTrackApplication {

    public static void main(String[] args) {
        
        // SpringApplication.run() bootstraps the application.
        //      - GreenTrackApplication.class  -> The primary configuration class.
        //      - args                                                 -> Command-line arguments forwarded.
        SpringApplication.run(GreenTrackApplication.class, args);
    }

}
