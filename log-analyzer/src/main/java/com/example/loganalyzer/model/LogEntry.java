package com.example.loganalyzer.model;

import java.time.LocalDateTime;

public class LogEntry {
    private final LocalDateTime timestamp;
    private final String level;
    private final StringBuilder message;

    public LogEntry(LocalDateTime timestamp, String level, String firstLine) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = new StringBuilder(firstLine);
    }

    /** Stack trace jaisi continuation lines isi entry me jud jati hain. */
    public void appendLine(String line) {
        message.append('\n').append(line);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getMessage() { return message.toString(); }
    public String getFirstLine() {
        int i = message.indexOf("\n");
        return i < 0 ? message.toString() : message.substring(0, i);
    }
}
