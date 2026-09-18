package com.vityarthi.library.model;

import java.util.Objects;

/**
 * Domain entity representing a Film record in the library catalog.
 */
public class Film {
    private int id;
    private String title;
    private String director;
    private String genre;
    private int releaseYear;

    public Film() {
    }

    public Film(int id, String title, String director, String genre, int releaseYear) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public Film(String title, String director, String genre, int releaseYear) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id &&
                releaseYear == film.releaseYear &&
                Objects.equals(title, film.title) &&
                Objects.equals(director, film.director) &&
                Objects.equals(genre, film.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, director, genre, releaseYear);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%d) - Dir: %s | Genre: %s", 
                id, title, releaseYear, director, genre);
    }
}
