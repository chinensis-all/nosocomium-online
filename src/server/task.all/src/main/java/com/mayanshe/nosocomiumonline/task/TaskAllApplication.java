package com.mayanshe.nosocomiumonline.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.mayanshe.nosocomiumonline")
public class TaskAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskAllApplication.class, args);
    }
}
