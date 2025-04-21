package com.example.utils;

import jakarta.annotation.PostConstruct;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProxyFactory {

    private final TimeInterceptor timeInterceptor;
    private final AnnotationScanner annotationScanner;

    public ProxyFactory(TimeInterceptor timeInterceptor, AnnotationScanner annotationScanner) {
        this.timeInterceptor = timeInterceptor;
        this.annotationScanner = annotationScanner;
    }

    public  Set<Object> createProxy(){
        Set<Object> proxiedObjects = new HashSet<>();
        Set<Method> annotatedMethods = annotationScanner.getAnnotatedMethods();

        if(!annotatedMethods.isEmpty()){
            Set<Class<?>> classes = annotatedMethods.stream()
                    .map(Method::getDeclaringClass)
                    .collect(Collectors.toSet());

            for(Class<?> clazz : classes){
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(timeInterceptor);

                Object proxy = enhancer.create();
                proxiedObjects.add(proxy);
            }
        }
        return proxiedObjects;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init(){
        Set<Object> objects = createProxy();
        objects.forEach(p -> System.out.println("Создан прокси: " + p.getClass()));
        }
}
