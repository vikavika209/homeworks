package com.example.metricStatProvider;

import com.example.entity.MethodMetricStat;
import com.example.service.MetricStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricStatProviderImplTest {

    private MetricStateService metricStateService;
    private MetricStatProviderImpl provider;

    @BeforeEach
    void setup() {
        metricStateService = mock(MetricStateService.class);
        provider = new MetricStatProviderImpl(metricStateService);
    }

    @Test
    void testGetTotalStatForPeriod_shouldReturnOnlyMatchingMetrics() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(10);
        LocalDateTime to = now.plusMinutes(10);

        MethodMetricStat stat1 = new MethodMetricStat("methodA");
        stat1.recordInvocation(now, 100);

        MethodMetricStat stat2 = new MethodMetricStat("methodB");
        stat2.recordInvocation(now.minusHours(1), 200);

        Map<String, MethodMetricStat> storage = new HashMap<>();
        storage.put("methodA", stat1);
        storage.put("methodB", stat2);

        when(metricStateService.getAllMetricStat()).thenReturn(storage);

        List<MethodMetricStat> result = provider.getTotalStatForPeriod(from, to);

        assertEquals(1, result.size());
        assertEquals("methodA", result.get(0).getMethodName());
    }

    @Test
    void testGetTotalStatByMethodForPeriod_shouldReturnIfWithinRange() {
        LocalDateTime now = LocalDateTime.now();
        MethodMetricStat stat = new MethodMetricStat("doWork");
        stat.recordInvocation(now, 120);

        when(metricStateService.getAllMetricStat()).thenReturn(
                Map.of("doWork", stat)
        );

        MethodMetricStat result = provider.getTotalStatByMethodForPeriod("doWork", now.minusMinutes(1), now.plusMinutes(1));
        assertNotNull(result);
        assertEquals("doWork", result.getMethodName());
    }

    @Test
    void testGetTotalStatByMethodForPeriod_shouldReturnNullIfOutsideRange() {
        LocalDateTime now = LocalDateTime.now();
        MethodMetricStat stat = new MethodMetricStat("doWork");
        stat.recordInvocation(now.minusHours(2), 300);

        when(metricStateService.getAllMetricStat()).thenReturn(
                Map.of("doWork", stat)
        );

        MethodMetricStat result = provider.getTotalStatByMethodForPeriod("doWork", now.minusMinutes(5), now.plusMinutes(5));
        assertNull(result);
    }
}
