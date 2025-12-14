package com.mayanshe.nosocomiumonline.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mayanshe.nosocomiumonline")
public class PatientApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientApiApplication.class, args);
    }
}
