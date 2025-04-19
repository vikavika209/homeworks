package com.example.config;

import com.example.service.MetricStateService;
import com.example.utils.AnnotationScanner;
import com.example.utils.ProxyFactory;
import com.example.utils.TimeInterceptor;
import com.example.processor.TimedBeanRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimedAutoConfig {

    @Bean
    TimedBeanRegistrar timedBeanRegistrar() {
        return new TimedBeanRegistrar(
                new ProxyFactory(
                        new TimeInterceptor(
                                new MetricStateService()),
                        new AnnotationScanner()),
                "com.example");
    }
}
