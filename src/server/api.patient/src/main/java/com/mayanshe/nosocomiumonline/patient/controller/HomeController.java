package com.mayanshe.nosocomiumonline.patient.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to Nosocomium Online Patient API!";
    }
}

