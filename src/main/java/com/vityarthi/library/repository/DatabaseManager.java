package com.vityarthi.library.repository;

import com.vityarthi.library.exception.DatabaseOperationException;
import com.vityarthi.library.util.AppLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connectivity and schema lifecycle.
 */
public class DatabaseManager {
    private static final String DEFAULT_URL = "jdbc:sqlite:cinematic_archive.db";
    private static String databaseUrl = DEFAULT_URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            AppLogger.error("SQLite JDBC Driver not found in classpath", e);
            throw new DatabaseOperationException("SQLite JDBC Driver not found", e);
        }
    }

    /**
     * Set a custom JDBC URL (useful for in-memory or isolated unit test databases).
     *
     * @param url JDBC connection URL
     */
    public static void setDatabaseUrl(String url) {
        databaseUrl = url;
    }

    /**
     * Reset to the default database URL.
     */
    public static void resetDatabaseUrl() {
        databaseUrl = DEFAULT_URL;
    }

    /**
     * Obtains an active connection to the SQLite database.
     *
     * @return active java.sql.Connection
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(databaseUrl);
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (SQLException e) {
            AppLogger.error("Failed to establish database connection to: " + databaseUrl, e);
            throw new DatabaseOperationException("Database connection error: " + e.getMessage(), e);
        }
    }

    /**
     * Idempotently builds the database tables if they do not already exist.
     */
    public static void initializeDatabase() {
        String createFilmsTable = "CREATE TABLE IF NOT EXISTS films ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "director TEXT,"
                + "genre TEXT,"
                + "release_year INTEGER);";

        String createReviewsTable = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "film_id INTEGER NOT NULL,"
                + "user_id INTEGER,"
                + "rating REAL NOT NULL,"
                + "comment TEXT,"
                + "FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createFilmsTable);
            stmt.execute(createReviewsTable);
            AppLogger.info("Database schema verified and initialized.");
        } catch (SQLException e) {
            AppLogger.error("Failed to initialize database schema", e);
            throw new DatabaseOperationException("Error initializing database schema", e);
        }
    }
}
