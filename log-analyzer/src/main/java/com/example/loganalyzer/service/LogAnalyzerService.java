package com.example.loganalyzer.service;

import com.example.loganalyzer.model.AnalysisResult;
import com.example.loganalyzer.model.AnalysisResult.TopItem;
import com.example.loganalyzer.model.LogEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private static final Pattern EXCEPTION = Pattern.compile("([A-Za-z0-9_.]+(?:Exception|Error))");
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");

    public AnalysisResult analyze(List<LogEntry> all, String level, String keyword, int topN) {
        List<LogEntry> entries = all.stream()
                .filter(e -> level == null || level.isBlank() || e.getLevel().equalsIgnoreCase(level))
                .filter(e -> keyword == null || keyword.isBlank()
                        || e.getMessage().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        Map<String, Long> levelCounts = new LinkedHashMap<>();
        for (String l : List.of("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL")) {
            long c = entries.stream().filter(e -> e.getLevel().equals(l)).count();
            if (c > 0) levelCounts.put(l, c);
        }

        LocalDateTime first = entries.stream().map(LogEntry::getTimestamp).min(Comparator.naturalOrder()).orElse(null);
        LocalDateTime last = entries.stream().map(LogEntry::getTimestamp).max(Comparator.naturalOrder()).orElse(null);

        Map<String, Long> topErrorsMap = entries.stream()
                .filter(e -> e.getLevel().equals("ERROR") || e.getLevel().equals("FATAL"))
                .collect(Collectors.groupingBy(e -> normalize(e.getFirstLine()), Collectors.counting()));

        Map<String, Long> exceptionMap = new HashMap<>();
        for (LogEntry e : entries) {
            Matcher m = EXCEPTION.matcher(e.getMessage());
            Set<String> seen = new HashSet<>();
            while (m.find()) {
                if (seen.add(m.group(1))) exceptionMap.merge(m.group(1), 1L, Long::sum);
            }
        }

        Map<String, Long> perHour = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getTimestamp().format(HOUR_FMT),
                        TreeMap::new, Collectors.counting()));

        return new AnalysisResult(entries.size(), first, last, levelCounts,
                top(topErrorsMap, topN), top(exceptionMap, topN), perHour);
    }

    /** Numbers ko '#' se replace karta hai taaki similar errors group ho jayein. */
    private String normalize(String s) {
        return s.replaceAll("\\d+", "#");
    }

    private List<TopItem> top(Map<String, Long> map, int n) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .map(e -> new TopItem(e.getKey(), e.getValue()))
                .toList();
    }
}
