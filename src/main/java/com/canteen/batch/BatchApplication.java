package com.canteen.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standard Spring Boot application entry point.
 * 
 * In a standard Spring Boot application, @SpringBootApplication annotation does three things:
 * 1. @Configuration: Tags the class as a source of bean definitions.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings. (Including Spring Batch's auto-config)
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, and services in the 'com.canteen.batch' package.
 */
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        // Runs the application. Assuming application.properties has spring.batch.job.enabled=true,
        // it will automatically trigger our configured Batch Job upon startup.
        System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));
    }
}
