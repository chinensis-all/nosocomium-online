package com.mayanshe.nosocomiumonline.doctor.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to Nosocomium Online Doctor API!";
    }
}

