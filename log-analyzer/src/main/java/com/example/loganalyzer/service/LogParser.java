package com.example.loganalyzer.service;

import com.example.loganalyzer.model.LogEntry;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supported format (Logback/Spring default jaisa):
 * 2024-01-15 10:23:45.123 ERROR [main] com.app.Service - message
 */
@Component
public class LogParser {

    private static final Pattern LINE = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2})[ T](\\d{2}:\\d{2}:\\d{2})(?:[.,]\\d+)?\\s+"
                    + "(TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL)\\s+(.*)$");

    public List<LogEntry> parse(InputStream in) throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            LogEntry current = null;
            while ((line = reader.readLine()) != null) {
                Matcher m = LINE.matcher(line);
                if (m.matches()) {
                    LocalDateTime ts = LocalDateTime.parse(m.group(1) + "T" + m.group(2));
                    String level = m.group(3).equals("WARNING") ? "WARN" : m.group(3);
                    current = new LogEntry(ts, level, m.group(4));
                    entries.add(current);
                } else if (current != null && !line.isBlank()) {
                    current.appendLine(line); // stack trace line
                }
            }
        }
        return entries;
    }
}
