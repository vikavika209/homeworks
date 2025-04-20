package com.example.service;

import com.example.entity.MethodMetricStat;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricStateService {
    private final Map<String, MethodMetricStat> methodMetricStatStorage = new ConcurrentHashMap<>();

    public MethodMetricStat saveMetricStat(MethodMetricStat methodMetricStat) {
        methodMetricStatStorage.put(methodMetricStat.getMethodName(), methodMetricStat);
        return methodMetricStat;
    }

    public Map<String, MethodMetricStat> getAllMetricStat(){
        return methodMetricStatStorage;
    }

    public MethodMetricStat getMethodMetricStat(Method method){
        return methodMetricStatStorage.get(method.getName());
    }

}
