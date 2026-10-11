package com.example.loganalyzer.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AnalysisResult(
        int totalEntries,
        LocalDateTime firstTimestamp,
        LocalDateTime lastTimestamp,
        Map<String, Long> levelCounts,
        List<TopItem> topErrors,
        List<TopItem> topExceptions,
        Map<String, Long> entriesPerHour) {

    public record TopItem(String key, long count) { }
}
