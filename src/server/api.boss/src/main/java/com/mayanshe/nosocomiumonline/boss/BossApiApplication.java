package com.mayanshe.nosocomiumonline.boss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mayanshe.nosocomiumonline")
public class BossApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BossApiApplication.class, args);
    }
}
