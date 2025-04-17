package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "ru");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DateConfig.class, PrintDateService.class);

        PrintDateService printDateService = context.getBean(PrintDateService.class);
        printDateService.today_iso(new Date());
        printDateService.today(new Date());

    }
}