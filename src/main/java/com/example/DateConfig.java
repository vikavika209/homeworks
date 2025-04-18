package com.example;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Configuration
public class DateConfig {

    @Bean(name = "localizedDateFormat")
    @Scope("prototype")
    @Profile("en")
    public SimpleDateFormat localizedDateFormatEn() {
        return new SimpleDateFormat("EEEE, d MMMM, yyyy", new Locale("en"));
    }

    @Bean(name = "localizedDateFormat")
    @Scope("prototype")
    @Profile("ru")
    public SimpleDateFormat localizedDateFormatRu() {
        return new SimpleDateFormat("EEEE, d MMMM, yyyy", new Locale("ru"));
    }

    @Bean(name = "standardDateFormat")
    @Primary
    @Scope("prototype")
    public SimpleDateFormat standardDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

}
