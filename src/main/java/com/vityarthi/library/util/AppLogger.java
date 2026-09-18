package com.vityarthi.library.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight structured console logger utility for production tracing.
 */
public final class AppLogger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
        // Utility class
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String thread = Thread.currentThread().getName();
        System.out.printf("[%s] [%s] [%s] %s%n", timestamp, thread, level, message);
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Throwable throwable) {
        log("ERROR", message + (throwable != null ? " - Cause: " + throwable.getMessage() : ""));
    }
}
