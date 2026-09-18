package com.vityarthi.library.repository;

import com.vityarthi.library.exception.DatabaseOperationException;
import com.vityarthi.library.model.Film;
import com.vityarthi.library.util.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object / Repository for Film entity persistence using SQLite.
 */
public class FilmRepository {

    public Film save(Film film) {
        String sql = "INSERT INTO films (title, director, genre, release_year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, film.getTitle());
            pstmt.setString(2, film.getDirector());
            pstmt.setString(3, film.getGenre());
            pstmt.setInt(4, film.getReleaseYear());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    film.setId(generatedKeys.getInt(1));
                }
            }
            return film;
        } catch (SQLException e) {
            AppLogger.error("Failed to persist film: " + film.getTitle(), e);
            throw new DatabaseOperationException("Error saving film: " + e.getMessage(), e);
        }
    }

    public Optional<Film> findById(int id) {
        String sql = "SELECT id, title, director, genre, release_year FROM films WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFilm(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            AppLogger.error("Failed to query film with ID: " + id, e);
            throw new DatabaseOperationException("Error fetching film by id: " + e.getMessage(), e);
        }
    }

    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id, title, director, genre, release_year FROM films ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                films.add(mapResultSetToFilm(rs));
            }
            return films;
        } catch (SQLException e) {
            AppLogger.error("Failed to retrieve all films", e);
            throw new DatabaseOperationException("Error fetching all films: " + e.getMessage(), e);
        }
    }

    public List<Film> findByGenre(String genre) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id, title, director, genre, release_year FROM films WHERE LOWER(genre) = LOWER(?) ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    films.add(mapResultSetToFilm(rs));
                }
            }
            return films;
        } catch (SQLException e) {
            AppLogger.error("Failed to filter films by genre: " + genre, e);
            throw new DatabaseOperationException("Error filtering films by genre: " + e.getMessage(), e);
        }
    }

    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM films WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            AppLogger.error("Failed checking existence of film with ID: " + id, e);
            throw new DatabaseOperationException("Error checking film existence: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.error("Failed to delete film with ID: " + id, e);
            throw new DatabaseOperationException("Error deleting film: " + e.getMessage(), e);
        }
    }

    private Film mapResultSetToFilm(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("director"),
                rs.getString("genre"),
                rs.getInt("release_year")
        );
    }
}
