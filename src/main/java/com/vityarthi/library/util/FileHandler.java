package com.vityarthi.library.util;

import com.vityarthi.library.model.Film;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File I/O utility providing CSV/flat-file serialization and deserialization for film data.
 */
public final class FileHandler {
    public static final String DEFAULT_FILE_NAME = "archive_data.txt";

    private FileHandler() {
        // Utility class
    }

    public static void saveFilms(List<Film> films) {
        saveFilms(films, DEFAULT_FILE_NAME);
    }

    public static void saveFilms(List<Film> films, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Film film : films) {
                writer.write(String.format("%s,%s,%s,%d",
                        film.getTitle(),
                        film.getDirector(),
                        film.getGenre(),
                        film.getReleaseYear()));
                writer.newLine();
            }
            AppLogger.info("Exported " + films.size() + " records successfully to " + fileName);
        } catch (IOException e) {
            AppLogger.error("Failed writing records to file: " + fileName, e);
        }
    }

    public static List<Film> loadFilms() {
        return loadFilms(DEFAULT_FILE_NAME);
    }

    public static List<Film> loadFilms(String fileName) {
        List<Film> loadedFilms = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            AppLogger.warn("File not found for import: " + fileName);
            return loadedFilms;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 4) {
                    try {
                        String title = data[0].trim();
                        String director = data[1].trim();
                        String genre = data[2].trim();
                        int year = Integer.parseInt(data[3].trim());
                        loadedFilms.add(new Film(title, director, genre, year));
                    } catch (NumberFormatException e) {
                        AppLogger.warn("Malformed release year on line " + lineNum + ": " + line);
                    }
                } else {
                    AppLogger.warn("Skipping invalid CSV column format on line " + lineNum + ": " + line);
                }
            }
            AppLogger.info("Imported " + loadedFilms.size() + " film entries from " + fileName);
        } catch (IOException e) {
            AppLogger.error("Error reading file: " + fileName, e);
        }
        return loadedFilms;
    }
}
