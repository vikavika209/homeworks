package com.example.utils;

import jakarta.annotation.PostConstruct;
import net.sf.cglib.proxy.Enhancer;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxyFactory {

    private final TimeInterceptor timeInterceptor;
    private final AnnotationScanner annotationScanner;

    public ProxyFactory(TimeInterceptor timeInterceptor, AnnotationScanner annotationScanner) {
        this.timeInterceptor = timeInterceptor;
        this.annotationScanner = annotationScanner;
    }

    public  Set<Object> createProxy(String packageName){
        Set<Object> proxiedObjects = new HashSet<>();
        Set<Method> annotatedMethods = annotationScanner.getAnnotatedMethods(packageName);

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

    @PostConstruct
    public void init() {
        createProxy("com.example");
    }
}
