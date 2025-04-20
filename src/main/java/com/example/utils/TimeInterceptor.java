package com.example.utils;

import com.example.entity.MethodMetricStat;
import com.example.service.MetricStateService;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeInterceptor implements MethodInterceptor {
    private final MetricStateService metricStateService;

    public TimeInterceptor(MetricStateService metricStateService) {
        this.metricStateService = metricStateService;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(">>> Фиксация метода: " + method.getName());
        long start = System.currentTimeMillis();
        Object result = methodProxy.invokeSuper(o, objects);
        long end = System.currentTimeMillis();

        LocalDateTime callTime = Instant.ofEpochMilli(start)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        long duration = end - start;

        MethodMetricStat methodMetricStat = metricStateService.getMethodMetricStat(method);

        if (methodMetricStat == null) {
            String methodName = method.getDeclaringClass().getName() + "#" + method.getName();
            methodMetricStat = new MethodMetricStat(methodName);
            metricStateService.saveMetricStat(methodMetricStat);
        }

        methodMetricStat.recordInvocation(callTime, duration);

        return result;
    }
}
