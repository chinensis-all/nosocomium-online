package com.mayanshe.nosocomiumonline.application.service;

import org.springframework.stereotype.Service;

@Service
public class DemoAppService {
    public String getDemo(Long id) {
        return "Demo " + id;
    }
}
