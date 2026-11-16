package com.radiantcat1.course2ics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Course2IcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Course2IcsApplication.class, args);
    }
}
