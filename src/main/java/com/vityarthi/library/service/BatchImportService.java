package com.vityarthi.library.service;

import com.vityarthi.library.model.Film;
import com.vityarthi.library.util.AppLogger;

import java.util.List;

/**
 * Concurrency worker that imports bulk film metadata asynchronously in the background.
 */
public class BatchImportService implements Runnable {
    private final List<Film> filmsToImport;
    private final LibraryService libraryService;
    private final long delayMillis;

    public BatchImportService(List<Film> filmsToImport, LibraryService libraryService) {
        this(filmsToImport, libraryService, 1200);
    }

    public BatchImportService(List<Film> filmsToImport, LibraryService libraryService, long delayMillis) {
        this.filmsToImport = filmsToImport;
        this.libraryService = libraryService;
        this.delayMillis = delayMillis;
    }

    @Override
    public void run() {
        AppLogger.info("Background batch ingestion task started for " + filmsToImport.size() + " films.");
        int successCount = 0;

        for (Film film : filmsToImport) {
            try {
                // Simulate I/O or network latency
                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }
                libraryService.addFilm(film);
                successCount++;
                AppLogger.info("Asynchronously ingested: '" + film.getTitle() + "'");
            } catch (InterruptedException e) {
                AppLogger.warn("Batch import thread was interrupted.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                AppLogger.error("Failed to import film '" + film.getTitle() + "': " + e.getMessage());
            }
        }
        AppLogger.info("Batch sync completed! Successfully processed: " + successCount + "/" + filmsToImport.size());
        System.out.print("\n> Batch sync complete! Select an option from the menu: ");
    }
}
