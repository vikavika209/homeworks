package com.example.processor;

import com.example.utils.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;


import java.util.Set;

public class TimedBeanRegistrar implements BeanDefinitionRegistryPostProcessor {
    private final ProxyFactory proxyFactory;
    private final String packageName;

    public TimedBeanRegistrar(ProxyFactory proxyFactory, String packageName) {
        this.proxyFactory = proxyFactory;
        this.packageName = packageName;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Object> proxiedBeans = proxyFactory.createProxy(packageName);

        for (Object proxy : proxiedBeans) {
            String beanName = proxy.getClass().getSuperclass().getName() + "#TimedProxy";
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((Class<?>) proxy.getClass());
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.setInstanceSupplier(() -> proxy);

            registry.registerBeanDefinition(beanName, definition);
            System.out.println("Зарегистрирован бин: " + beanName);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
