package com.vityarthi.library.service;

import com.vityarthi.library.exception.InvalidDataException;
import com.vityarthi.library.exception.ResourceNotFoundException;
import com.vityarthi.library.model.Film;
import com.vityarthi.library.model.Review;
import com.vityarthi.library.repository.FilmRepository;
import com.vityarthi.library.repository.ReviewRepository;
import com.vityarthi.library.util.AppLogger;

import java.util.List;

/**
 * Core business logic service for managing the library catalog, reviews, and validation rules.
 */
public class LibraryService {
    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;

    public LibraryService() {
        this(new FilmRepository(), new ReviewRepository());
    }

    public LibraryService(FilmRepository filmRepository, ReviewRepository reviewRepository) {
        this.filmRepository = filmRepository;
        this.reviewRepository = reviewRepository;
    }

    public Film addFilm(String title, String director, String genre, int releaseYear) {
        validateFilmAttributes(title, director, genre, releaseYear);
        Film film = new Film(title.trim(), director.trim(), genre.trim(), releaseYear);
        Film saved = filmRepository.save(film);
        AppLogger.info("Film added successfully: [" + saved.getId() + "] " + saved.getTitle());
        return saved;
    }

    public Film addFilm(Film film) {
        if (film == null) {
            throw new InvalidDataException("Film entity cannot be null.");
        }
        return addFilm(film.getTitle(), film.getDirector(), film.getGenre(), film.getReleaseYear());
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(int id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found with ID: " + id));
    }

    public List<Film> getFilmsByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new InvalidDataException("Genre query cannot be blank.");
        }
        return filmRepository.findByGenre(genre.trim());
    }

    public Review addReview(int filmId, int userId, double rating, String comment) {
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException("Cannot review non-existent film with ID: " + filmId);
        }
        if (rating < 1.0 || rating > 5.0) {
            throw new InvalidDataException("Rating must be between 1.0 and 5.0. Provided: " + rating);
        }
        String sanitizedComment = (comment != null) ? comment.trim() : "";
        Review review = new Review(filmId, userId, rating, sanitizedComment);
        Review saved = reviewRepository.save(review);
        AppLogger.info("Review saved for film ID " + filmId + " with rating " + rating);
        return saved;
    }

    public Review addReview(Review review) {
        if (review == null) {
            throw new InvalidDataException("Review entity cannot be null.");
        }
        return addReview(review.getFilmId(), review.getUserId(), review.getRating(), review.getComment());
    }

    public List<Review> getReviewsForFilm(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException("Film not found with ID: " + filmId);
        }
        return reviewRepository.findByFilmId(filmId);
    }

    public double getAverageRating(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException("Film not found with ID: " + filmId);
        }
        return reviewRepository.calculateAverageRating(filmId);
    }

    public void displayAllFilms() {
        List<Film> films = getAllFilms();
        if (films.isEmpty()) {
            System.out.println("The archive catalog is currently empty.");
            return;
        }
        System.out.println("\n--- Cinematic Archive Catalog (Database Records) ---");
        for (Film film : films) {
            System.out.println(film);
        }
    }

    public void displayFilmsByGenre(String genre) {
        List<Film> films = getFilmsByGenre(genre);
        System.out.println("\n--- Filtering by Genre: " + genre + " ---");
        if (films.isEmpty()) {
            System.out.println("No films found under genre: " + genre);
            return;
        }
        for (Film film : films) {
            System.out.println(film);
        }
    }

    public void displayReviewsForFilm(int filmId) {
        Film film = getFilmById(filmId);
        List<Review> reviews = getReviewsForFilm(filmId);
        double avg = getAverageRating(filmId);

        System.out.println("\n--- Reviews for: " + film.getTitle() + " (ID: " + filmId + ") ---");
        if (reviews.isEmpty()) {
            System.out.println("No reviews found for this film.");
            return;
        }
        System.out.printf("Average Rating: %.2f / 5.0 (%d total reviews)%n", avg, reviews.size());
        for (Review review : reviews) {
            System.out.println(review);
        }
    }

    private void validateFilmAttributes(String title, String director, String genre, int releaseYear) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Film title cannot be empty.");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new InvalidDataException("Director cannot be empty.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new InvalidDataException("Genre cannot be empty.");
        }
        if (releaseYear < 1888 || releaseYear > 2100) {
            throw new InvalidDataException("Release year must be between 1888 and 2100. Provided: " + releaseYear);
        }
    }
}
