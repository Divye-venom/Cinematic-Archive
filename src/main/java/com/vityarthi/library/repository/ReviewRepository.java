package com.vityarthi.library.repository;

import com.vityarthi.library.exception.DatabaseOperationException;
import com.vityarthi.library.model.Review;
import com.vityarthi.library.util.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object / Repository for Review entity persistence using SQLite.
 */
public class ReviewRepository {

    public Review save(Review review) {
        String sql = "INSERT INTO reviews (film_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, review.getFilmId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setDouble(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getInt(1));
                }
            }
            return review;
        } catch (SQLException e) {
            AppLogger.error("Failed to persist review for film ID: " + review.getFilmId(), e);
            throw new DatabaseOperationException("Error saving review: " + e.getMessage(), e);
        }
    }

    public List<Review> findByFilmId(int filmId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, film_id, user_id, rating, comment FROM reviews WHERE film_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
            return reviews;
        } catch (SQLException e) {
            AppLogger.error("Failed fetching reviews for film ID: " + filmId, e);
            throw new DatabaseOperationException("Error fetching reviews by film id: " + e.getMessage(), e);
        }
    }

    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, film_id, user_id, rating, comment FROM reviews ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
            return reviews;
        } catch (SQLException e) {
            AppLogger.error("Failed fetching all reviews", e);
            throw new DatabaseOperationException("Error fetching all reviews: " + e.getMessage(), e);
        }
    }

    public double calculateAverageRating(int filmId) {
        String sql = "SELECT AVG(rating) FROM reviews WHERE film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            return 0.0;
        } catch (SQLException e) {
            AppLogger.error("Failed calculating average rating for film ID: " + filmId, e);
            throw new DatabaseOperationException("Error calculating average rating: " + e.getMessage(), e);
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("id"),
                rs.getInt("film_id"),
                rs.getInt("user_id"),
                rs.getDouble("rating"),
                rs.getString("comment")
        );
    }
}
