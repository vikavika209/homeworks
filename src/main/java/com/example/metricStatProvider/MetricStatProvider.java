package com.example.metricStatProvider;

import com.example.entity.MethodMetricStat;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricStatProvider {
    List<MethodMetricStat> getTotalStatForPeriod(LocalDateTime from, LocalDateTime to);
    MethodMetricStat getTotalStatByMethodForPeriod(String method, LocalDateTime from, LocalDateTime to);
}
