package com.example.loganalyzer.controller;

import com.example.loganalyzer.model.AnalysisResult;
import com.example.loganalyzer.model.LogEntry;
import com.example.loganalyzer.service.LogAnalyzerService;
import com.example.loganalyzer.service.LogParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogParser parser;
    private final LogAnalyzerService analyzer;

    public LogController(LogParser parser, LogAnalyzerService analyzer) {
        this.parser = parser;
        this.analyzer = analyzer;
    }

    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestParam("file") MultipartFile file,
                                  @RequestParam(required = false) String level,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "5") int top) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File empty hai");
        }
        try {
            List<LogEntry> entries = parser.parse(file.getInputStream());
            return analyzer.analyze(entries, level, keyword, Math.max(1, top));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File padhne me error", e);
        }
    }
}
