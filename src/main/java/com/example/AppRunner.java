package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final TestService testService;

    public AppRunner(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void run(String... args) {
        testService.doSomething();
    }
}

