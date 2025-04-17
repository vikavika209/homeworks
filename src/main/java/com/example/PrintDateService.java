package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrintDateService {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(DateConfig.class);

    public void today(Locale locale, Date date) {
        SimpleDateFormat sdf = applicationContext.getBean("enBean", SimpleDateFormat.class);

        if (locale.getLanguage().equalsIgnoreCase("ru")){
            sdf = applicationContext.getBean("ruBean", SimpleDateFormat.class);
        }

        System.out.println(sdf.format(date));
    };

    public void today_iso(Date date) {
        SimpleDateFormat sdf = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(sdf.format(date));
    };
}
