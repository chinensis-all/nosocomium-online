package com.mayanshe.nosocomiumonline.doctor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mayanshe.nosocomiumonline")
public class DoctorApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoctorApiApplication.class, args);
    }
}
