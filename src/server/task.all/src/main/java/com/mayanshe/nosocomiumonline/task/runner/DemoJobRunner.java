package com.mayanshe.nosocomiumonline.task.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoJobRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("DemoJobRunner executed");
    }
}
