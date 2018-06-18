package com.example.service.impl;

import com.example.service.interfaces.ExampleService;
import org.springframework.stereotype.Service;

@Service(value = "exampleService")
public class ExampleServiceImpl implements ExampleService {
    @Override
    public Integer exampleMethod(Integer i) {
        return 2 * i;
    }
}