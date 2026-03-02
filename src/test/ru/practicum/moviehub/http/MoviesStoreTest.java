package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

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

    @Test
    public void testClear() throws IOException, InterruptedException {
        MoviesStore moviesStore = new MoviesStore(); // Создаём экземпляр MoviesStore
        moviesStore.addMovie(new Movie("Inception", 2010, "Sci-Fi", 8.8));
        moviesStore.addMovie(new Movie("Interstellar", 2014, "Sci-Fi", 8.6));
        assertEquals(2, moviesStore.getAllMovies().size());
        moviesStore.clear();
        assertTrue(moviesStore.getAllMovies().isEmpty());
        assertEquals(0, moviesStore.idCounter);
    }

}
