package com.vityarthi.library;

import com.vityarthi.library.exception.LibraryException;
import com.vityarthi.library.model.Film;
import com.vityarthi.library.repository.DatabaseManager;
import com.vityarthi.library.service.BatchImportService;
import com.vityarthi.library.service.LibraryService;
import com.vityarthi.library.util.AppLogger;
import com.vityarthi.library.util.FileHandler;
import com.vityarthi.library.util.InputValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command-Line Interface (CLI) entry point for the Cinematic Archive & Review Engine.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryService libraryService = new LibraryService();

        // Ensure database schema is prepared
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
            AppLogger.error("Failed to initialize database. Exiting...", e);
            System.err.println("Fatal: Database initialization failed. Check your configuration.");
            return;
        }

        System.out.println("==========================================================");
        System.out.println("   Welcome to Cinematic Archive & Review Engine (CLI)    ");
        System.out.println("==========================================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputValidator.readInt(scanner, "Select an option (1-8): ", "Error: Please enter a valid number (1-8).");

            switch (choice) {
                case 1:
                    handleAddFilm(scanner, libraryService);
                    break;
                case 2:
                    libraryService.displayAllFilms();
                    break;
                case 3:
                    handleFilterByGenre(scanner, libraryService);
                    break;
                case 4:
                    handleLeaveReview(scanner, libraryService);
                    break;
                case 5:
                    handleViewReviews(scanner, libraryService);
                    break;
                case 6:
                    handleBatchImport(libraryService);
                    break;
                case 7:
                    handleExportData(libraryService);
                    break;
                case 8:
                    System.out.println("\nThank you for using Cinematic Archive. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option between 1 and 8.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add a new Film");
        System.out.println("2. View all Films");
        System.out.println("3. Filter Films by Genre");
        System.out.println("4. Leave a Review");
        System.out.println("5. View Reviews for a Film");
        System.out.println("6. Run Background Batch Import (Concurrency)");
        System.out.println("7. Export Archive Data to File");
        System.out.println("8. Exit");
    }

    private static void handleAddFilm(Scanner scanner, LibraryService service) {
        System.out.println("\n--- Add a New Film ---");
        String title = InputValidator.readNonEmptyString(scanner, "Title: ", "Error: Title cannot be empty.");
        String director = InputValidator.readNonEmptyString(scanner, "Director: ", "Error: Director cannot be empty.");
        String genre = InputValidator.readNonEmptyString(scanner, "Genre: ", "Error: Genre cannot be empty.");
        int year = InputValidator.readInt(scanner, "Release Year (e.g., 2024): ", "Error: Please enter a valid 4-digit year.");

        try {
            Film created = service.addFilm(title, director, genre, year);
            System.out.println("Success: Added [" + created.getId() + "] " + created.getTitle() + " to the archive.");
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleFilterByGenre(Scanner scanner, LibraryService service) {
        String genre = InputValidator.readNonEmptyString(scanner, "Enter genre to filter by: ", "Error: Genre cannot be empty.");
        service.displayFilmsByGenre(genre);
    }

    private static void handleLeaveReview(Scanner scanner, LibraryService service) {
        System.out.println("\n--- Leave a Review ---");
        service.displayAllFilms();
        int filmId = InputValidator.readInt(scanner, "Enter Film ID (number in brackets): ", "Error: Please enter a valid ID.");

        try {
            // Verify film exists
            service.getFilmById(filmId);

            double rating = InputValidator.readDouble(scanner, "Enter rating (1.0 to 5.0): ", "Error: Please enter a valid decimal rating.");
            System.out.print("Enter review critique: ");
            String comment = scanner.nextLine();

            service.addReview(filmId, 1, rating, comment);
            System.out.println("Success: Review registered for film ID " + filmId);
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleViewReviews(Scanner scanner, LibraryService service) {
        service.displayAllFilms();
        int filmId = InputValidator.readInt(scanner, "Enter Film ID to view reviews: ", "Error: Please enter a valid ID.");
        try {
            service.displayReviewsForFilm(filmId);
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleBatchImport(LibraryService service) {
        List<Film> sampleFilms = Arrays.asList(
                new Film("Interstellar", "Christopher Nolan", "Sci-Fi", 2014),
                new Film("Parasite", "Bong Joon-ho", "Thriller", 2019),
                new Film("Spirited Away", "Hayao Miyazaki", "Animation", 2001),
                new Film("The Dark Knight", "Christopher Nolan", "Action", 2008),
                new Film("Whiplash", "Damien Chazelle", "Drama", 2014)
        );

        BatchImportService task = new BatchImportService(sampleFilms, service);
        Thread worker = new Thread(task, "Worker-BatchImport");
        worker.setDaemon(true);
        worker.start();
        System.out.println("Background worker launched! You can continue using the menu while ingestion proceeds.");
    }

    private static void handleExportData(LibraryService service) {
        List<Film> films = service.getAllFilms();
        FileHandler.saveFilms(films);
        System.out.println("Successfully exported " + films.size() + " films to " + FileHandler.DEFAULT_FILE_NAME);
    }
}
