package com.vityarthi.library.model;

import java.util.Objects;

/**
 * Domain entity representing a user review relationally linked to a film.
 */
public class Review {
    private int id;
    private int filmId;
    private int userId;
    private double rating; // Scale: 1.0 to 5.0
    private String comment;

    public Review() {
    }

    public Review(int id, int filmId, int userId, double rating, String comment) {
        this.id = id;
        this.filmId = filmId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review(int filmId, int userId, double rating, String comment) {
        this.filmId = filmId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id &&
                filmId == review.filmId &&
                userId == review.userId &&
                Double.compare(review.rating, rating) == 0 &&
                Objects.equals(comment, review.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filmId, userId, rating, comment);
    }

    @Override
    public String toString() {
        return String.format("Rating: %.1f/5.0 | %s", rating, comment);
    }
}
