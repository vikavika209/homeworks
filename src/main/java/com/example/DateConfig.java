package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Configuration
public class DateConfig {

    @Bean(name = "enBean")
    @Scope("prototype")
    public SimpleDateFormat dateFormatEn() {
        return new SimpleDateFormat("EEEE, d MMMM, yyyy", new Locale("en"));
    }

    @Bean(name = "ruBean")
    @Scope("prototype")
    public SimpleDateFormat dateFormatRu() {
        return new SimpleDateFormat("EEEE, d MMMM, yyyy", new Locale("ru"));
    }

    @Bean(name = "standard")
    @Primary
    @Scope("prototype")
    public SimpleDateFormat standardDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
