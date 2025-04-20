package com.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodMetricStat {
    private final String methodName;
    private Integer invocationsCount = 0;
    private long totalTime = 0;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = Long.MIN_VALUE;
    List<LocalDateTime> timestamps = new ArrayList<>();

    public MethodMetricStat(String methodName) {
        this.methodName = methodName;
    }

    public void recordInvocation(LocalDateTime timestamp, long executionTime) {
        invocationsCount++;
        timestamps.add(timestamp);
        totalTime += executionTime;
        minTime = Math.min(executionTime, minTime);
        maxTime = Math.max(executionTime, maxTime);
    }

    public String getMethodName() {
        return methodName;
    }

    public Integer getInvocationsCount() {
        return invocationsCount;
    }

    public Integer getAverageTime() {
        return invocationsCount == 0 ? 0 : (int) totalTime / invocationsCount;
    }

    public Integer getMinTime() {
        return invocationsCount == 0 ? 0 : (int) minTime;
    }

    public Integer getMaxTime() {
        return invocationsCount == 0 ? 0 : (int) maxTime;
    }

    public List<LocalDateTime> getTimestamps() {
        return timestamps;
    }
}
