package com.vityarthi.library;

import com.vityarthi.library.exception.InvalidDataException;
import com.vityarthi.library.exception.ResourceNotFoundException;
import com.vityarthi.library.model.Film;
import com.vityarthi.library.model.Review;
import com.vityarthi.library.repository.DatabaseManager;
import com.vityarthi.library.repository.FilmRepository;
import com.vityarthi.library.repository.ReviewRepository;
import com.vityarthi.library.service.LibraryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Automated JUnit 5 test suite for LibraryService business logic and validation rules.
 */
class LibraryServiceTest {

    private static final String TEST_DB_PATH = "target/test_cinematic_archive.db";
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        // Ensure clean test database in target folder
        File targetDir = new File("target");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File testDb = new File(TEST_DB_PATH);
        if (testDb.exists()) {
            testDb.delete();
        }

        DatabaseManager.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_PATH);
        DatabaseManager.initializeDatabase();

        FilmRepository filmRepository = new FilmRepository();
        ReviewRepository reviewRepository = new ReviewRepository();
        libraryService = new LibraryService(filmRepository, reviewRepository);
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.resetDatabaseUrl();
        File testDb = new File(TEST_DB_PATH);
        if (testDb.exists()) {
            testDb.delete();
        }
    }

    @Test
    @DisplayName("Should successfully add a film and retrieve it by ID")
    void testAddFilmSuccess() {
        Film saved = libraryService.addFilm("Inception", "Christopher Nolan", "Sci-Fi", 2010);

        assertTrue(saved.getId() > 0, "Saved film should have an auto-generated positive ID");
        assertEquals("Inception", saved.getTitle());
        assertEquals("Christopher Nolan", saved.getDirector());
        assertEquals("Sci-Fi", saved.getGenre());
        assertEquals(2010, saved.getReleaseYear());

        Film fetched = libraryService.getFilmById(saved.getId());
        assertNotNull(fetched);
        assertEquals(saved.getId(), fetched.getId());
        assertEquals("Inception", fetched.getTitle());
    }

    @Test
    @DisplayName("Should reject film creation when title is blank")
    void testAddFilmWithBlankTitle() {
        InvalidDataException ex = assertThrows(InvalidDataException.class, () ->
                libraryService.addFilm("   ", "Director", "Action", 2020)
        );
        assertTrue(ex.getMessage().contains("Film title cannot be empty"));
    }

    @Test
    @DisplayName("Should reject film creation when release year is invalid")
    void testAddFilmWithInvalidYear() {
        assertThrows(InvalidDataException.class, () ->
                libraryService.addFilm("Title", "Director", "Action", 1800)
        );

        assertThrows(InvalidDataException.class, () ->
                libraryService.addFilm("Title", "Director", "Action", 2200)
        );
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent film ID")
    void testGetFilmByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> libraryService.getFilmById(99999));
    }

    @Test
    @DisplayName("Should filter films by genre case-insensitively")
    void testFilterByGenre() {
        libraryService.addFilm("Dune", "Denis Villeneuve", "Sci-Fi", 2021);
        libraryService.addFilm("Interstellar", "Christopher Nolan", "Sci-Fi", 2014);
        libraryService.addFilm("The Godfather", "Francis Ford Coppola", "Drama", 1972);

        List<Film> sciFiFilms = libraryService.getFilmsByGenre("sci-fi");
        assertEquals(2, sciFiFilms.size());

        List<Film> dramaFilms = libraryService.getFilmsByGenre("DRAMA");
        assertEquals(1, dramaFilms.size());
        assertEquals("The Godfather", dramaFilms.get(0).getTitle());
    }

    @Test
    @DisplayName("Should successfully submit and retrieve a review for an existing film")
    void testAddReviewSuccess() {
        Film film = libraryService.addFilm("Oppenheimer", "Christopher Nolan", "Biography", 2023);
        Review review = libraryService.addReview(film.getId(), 1, 4.8, "Masterpiece of cinematic tension.");

        assertTrue(review.getId() > 0);
        assertEquals(film.getId(), review.getFilmId());
        assertEquals(4.8, review.getRating());

        List<Review> reviews = libraryService.getReviewsForFilm(film.getId());
        assertEquals(1, reviews.size());
        assertEquals("Masterpiece of cinematic tension.", reviews.get(0).getComment());
    }

    @Test
    @DisplayName("Should reject reviews with ratings outside 1.0 to 5.0")
    void testAddReviewInvalidRating() {
        Film film = libraryService.addFilm("Memento", "Christopher Nolan", "Thriller", 2000);

        assertThrows(InvalidDataException.class, () ->
                libraryService.addReview(film.getId(), 1, 0.5, "Too low")
        );

        assertThrows(InvalidDataException.class, () ->
                libraryService.addReview(film.getId(), 1, 5.5, "Too high")
        );
    }

    @Test
    @DisplayName("Should reject review when film ID does not exist")
    void testAddReviewNonExistentFilm() {
        assertThrows(ResourceNotFoundException.class, () ->
                libraryService.addReview(8888, 1, 4.0, "Ghost film review")
        );
    }

    @Test
    @DisplayName("Should accurately compute average rating across multiple reviews")
    void testCalculateAverageRating() {
        Film film = libraryService.addFilm("The Matrix", "Lana & Lilly Wachowski", "Sci-Fi", 1999);
        libraryService.addReview(film.getId(), 1, 4.0, "Great concept.");
        libraryService.addReview(film.getId(), 2, 5.0, "Iconic visual effects.");

        double avg = libraryService.getAverageRating(film.getId());
        assertEquals(4.5, avg, 0.001);
    }
}
