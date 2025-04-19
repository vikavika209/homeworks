package com.example;

import com.example.annotation.Timed;

@Timed
public class TestService {
    public void doSomething() {
        System.out.println(">> Выполняется TestService#doSomething");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(">> TestService#doSomething завершён");
    }
}