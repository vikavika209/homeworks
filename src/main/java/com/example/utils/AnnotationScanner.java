package com.example.utils;


import com.example.annotation.Timed;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class AnnotationScanner {

    public Set<Method> getAnnotatedMethods(String packageName) {

        Set<Method> annotatedMethods = new HashSet<>();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(packageName)
                        .addScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner())
        );

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Timed.class);
        for (Method method : methods) {
            annotatedMethods.add(method);
        }

        Set<Class<?>> classes = getAnnotatedClasses(packageName);
        Set<Method> methodsFromClasses = getAnnotatedMethods(classes);
        annotatedMethods.addAll(methodsFromClasses);

        return annotatedMethods;
    }

    private Set<Method> getAnnotatedMethods(Set<Class<?>> classes){
        Set<Method> methods = new HashSet<>();

        for(Class<?> clazz : classes){
            if (clazz.isAnnotationPresent(Timed.class)){
                for (Method method : clazz.getDeclaredMethods()){
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    private Set<Class<?>> getAnnotatedClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Timed.class);
    }
}
