package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesStore {
    private List<Movie> movies;

    public MoviesStore() {
        movies = new ArrayList<>();
    }

    // Добавление фильма
    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    // Поиск фильма по названию
    public Movie findMovieByTitle(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null; // Если фильм не найден
    }

    // Удаление фильма по названию
    public boolean removeMovieByTitle(String title) {
        Movie movieToRemove = findMovieByTitle(title);
        if (movieToRemove != null) {
            movies.remove(movieToRemove);
            return true;
        }
        return false; // Если фильм не найден и не удалён
    }public List<Movie> getAllMovies() {
        return movies;
    }
}
