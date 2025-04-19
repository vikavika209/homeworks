package com.example.metricStatProvider;

import com.example.entity.MethodMetricStat;
import com.example.service.MetricStateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MetricStatProviderImpl implements MetricStatProvider {
    private final MetricStateService metricStateService;

    public MetricStatProviderImpl(MetricStateService metricStateService) {
        this.metricStateService = metricStateService;
    }

    @Override
    public List<MethodMetricStat> getTotalStatForPeriod(LocalDateTime from, LocalDateTime to) {
        return metricStateService.getAllMetricStat().entrySet().stream()
                .filter(entry -> entry.getValue().getTimestamps().stream()
                        .anyMatch(date -> isInPeriod(date, from, to)))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public MethodMetricStat getTotalStatByMethodForPeriod(String method, LocalDateTime from, LocalDateTime to) {
        return metricStateService.getAllMetricStat().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(method))
                .filter(entry -> entry.getValue().getTimestamps().stream()
                .anyMatch(date -> isInPeriod(date, from, to)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean isInPeriod(LocalDateTime date, LocalDateTime from, LocalDateTime to) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
