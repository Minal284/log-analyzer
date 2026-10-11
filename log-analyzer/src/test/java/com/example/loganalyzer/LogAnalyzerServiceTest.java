package com.example.loganalyzer;

import com.example.loganalyzer.model.AnalysisResult;
import com.example.loganalyzer.model.LogEntry;
import com.example.loganalyzer.service.LogAnalyzerService;
import com.example.loganalyzer.service.LogParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogAnalyzerServiceTest {

    private static final String LOG = """
            2024-01-15 10:00:01.100 INFO  [main] App - started
            2024-01-15 10:20:45.400 ERROR [t1] OrderService - Failed order 1
            java.lang.NullPointerException: boom
                at com.app.X.y(X.java:1)
            2024-01-15 10:30:45.400 ERROR [t1] OrderService - Failed order 2
            """;

    @Test
    void analyzesLevelsAndExceptions() throws Exception {
        List<LogEntry> entries = new LogParser()
                .parse(new ByteArrayInputStream(LOG.getBytes(StandardCharsets.UTF_8)));
        AnalysisResult r = new LogAnalyzerService().analyze(entries, null, null, 5);

        assertEquals(3, r.totalEntries());
        assertEquals(2L, r.levelCounts().get("ERROR"));
        assertEquals(2L, r.topErrors().get(0).count()); // "Failed order #" group hua
        assertEquals("java.lang.NullPointerException", r.topExceptions().get(0).key());
    }
}
