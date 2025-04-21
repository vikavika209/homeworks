package com.example.utils;


import com.example.annotation.Timed;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AnnotationScanner implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Set<Method> getAnnotatedMethods() {
        return Stream.concat(annotatedMethods().stream(),
                annotatedMethodsFromClass().stream())
                .collect(Collectors.toSet());
    }

    private Set<Method> annotatedMethods() {
        Set<Method> annotatedMethods = new HashSet<>();

        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Method[] methods = bean.getClass().getMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Timed.class)) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }



    private Set<Method> annotatedMethodsFromClass() {
        Set<Method> annotatedMethods = new HashSet<>();

        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Timed.class);

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Method[] methods = bean.getClass().getMethods();
            annotatedMethods.addAll(Arrays.asList(methods));
        }
        return annotatedMethods;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
//
//        Set<Method> annotatedMethods = new HashSet<>();
//
//        Reflections reflections = new Reflections(
//                new ConfigurationBuilder()
//                        .forPackages(packageName)
//                        .addScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner())
//        );
//
//        Set<Method> methods = reflections.getMethodsAnnotatedWith(Timed.class);
//        for (Method method : methods) {
//            annotatedMethods.add(method);
//        }
//
//        Set<Class<?>> classes = getAnnotatedClasses(packageName);
//        Set<Method> methodsFromClasses = getAnnotatedMethods(classes);
//        annotatedMethods.addAll(methodsFromClasses);
//
//        return annotatedMethods;
//    }
//
//    private Set<Method> getAnnotatedMethods(Set<Class<?>> classes){
//        Set<Method> methods = new HashSet<>();
//
//        for(Class<?> clazz : classes){
//            if (clazz.isAnnotationPresent(Timed.class)){
//                for (Method method : clazz.getDeclaredMethods()){
//                    methods.add(method);
//                }
//            }
//        }
//        return methods;
//    }
//
//    private Set<Class<?>> getAnnotatedClasses(String packageName) {
//        Reflections reflections = new Reflections(packageName);
//        return reflections.getTypesAnnotatedWith(Timed.class);
//    }


