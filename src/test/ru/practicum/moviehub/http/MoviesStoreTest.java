package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesStoreTest {
    @Test
    void testAddAndRemoveMovie() {
        MoviesStore moviesStore = new MoviesStore();
        Movie movie = new Movie("Title",  2023,"Genre", 8.5);
        moviesStore.addMovie(movie);
        assertTrue(moviesStore.findMovieByTitle(movie.getTitle()) != null);
        assertEquals(1, moviesStore.getAllMovies().size());
        moviesStore.removeMovieByTitle(movie.getTitle());
        assertTrue(moviesStore.findMovieByTitle(movie.getTitle()) == null);
    }
}
