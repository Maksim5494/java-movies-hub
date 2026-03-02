package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoviesStore {
    private final List<Movie> movies = new ArrayList<>();
    public long idCounter = 1;

    public void addMovie(Movie movie) {
        movie.setId(idCounter++);
        movies.add(movie);
    }

    public Optional<Movie> getById(long id) {
        return movies.stream().filter(m -> m.getId() == id).findFirst();
    }

    public boolean removeMovieById(long id) {
        return movies.removeIf(movie -> movie.getId() == id);
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    public Movie findMovieByTitle(String title) {
        return movies.stream()
                .filter(m -> m.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    public boolean removeMovieByTitle(String title) {
        return movies.removeIf(m -> m.getTitle().equals(title));
    }

    public void clear() {
        movies.clear();
        idCounter = 0;
    }
}