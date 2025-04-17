package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        PrintDateService printDateService = new PrintDateService();

        Date currentDate = new Date();
        Locale ruLocale = new Locale("ru");
        Locale enLocale = new Locale("en");

        printDateService.today_iso(currentDate);
        printDateService.today(ruLocale, currentDate);
        printDateService.today(enLocale, currentDate);
    }
}