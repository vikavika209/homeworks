package com.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PrintDateService {
    private final SimpleDateFormat localizedDateFormat;
    private final SimpleDateFormat standardDateFormat;

    public PrintDateService(@Qualifier("localizedDateFormat") SimpleDateFormat localizedDateFormat,
                            @Qualifier ("standardDateFormat") SimpleDateFormat standardDateFormat) {
        this.localizedDateFormat = localizedDateFormat;
        this.standardDateFormat = standardDateFormat;
    }

    public void today (Date date) {
        System.out.println(localizedDateFormat.format(date));
    }

    public void today_iso (Date date) {
        System.out.println(standardDateFormat.format(date));
    }


}
