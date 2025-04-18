package com.example;



import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PrintDateService {
    private final SimpleDateFormat localizedDateFormat;
    private final SimpleDateFormat standardDateFormat;
    private final Date date = new Date();

    public PrintDateService(@Qualifier("localizedDateFormat") SimpleDateFormat localizedDateFormat,
                            @Qualifier ("standardDateFormat") SimpleDateFormat standardDateFormat) {
        this.localizedDateFormat = localizedDateFormat;
        this.standardDateFormat = standardDateFormat;
    }

    public void today () {
        System.out.println(localizedDateFormat.format(date));
    }

    public void today_iso () {
        System.out.println(standardDateFormat.format(date));
    }

    @PostConstruct
    public void init() {
        today();
        today_iso();
    }

}
