package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoviesStore {
    private List<Movie> movies;

    public MoviesStore() {

        movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public Movie findMovieByTitle(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    public boolean updateMovie(Movie updatedMovie) {
        Movie existingMovie = findMovieByTitle(updatedMovie.getTitle());
        if (existingMovie != null) {
            existingMovie.setYear(updatedMovie.getYear());
            existingMovie.setGenre(updatedMovie.getGenre());
            existingMovie.setRating(updatedMovie.getRating());
            return true;
        }
        return false;
    }

    public boolean removeMovieById(long id) {
        for (Movie movie : movies) {
            if (movie.getId() == id) {
                movies.remove(movie);
                return true;
            }
        }
        return false;
    }

    public List<Movie> findMoviesByYear(int year) {
        return movies.stream()
                .filter(movie -> movie.getYear() == year)
                .collect(Collectors.toList());
    }

    public List<Movie> findMoviesByGenre(String genre) {
        return movies.stream()
                .filter(movie -> genre.equalsIgnoreCase(movie.getGenre()))
                .collect(Collectors.toList());
    }



    public Movie getMovieByIndex(int index) {
        if (index >= 0 && index < movies.size()) {
            return movies.get(index);
        } else {
            throw new IndexOutOfBoundsException("Индекс выходит за пределы списка фильмов");
        }
    }

    public int getMoviesCount() {
        return movies.size();
    }

    public boolean updateMovieRating(String title, double newRating) {
        Movie movie = findMovieByTitle(title);
        if (movie != null) {
            movie.setRating(newRating);
            return true;
        }
        return false;
    }


    public boolean removeMovieByTitle(String title) {
        Movie movieToRemove = findMovieByTitle(title);
        if (movieToRemove != null) {
            movies.remove(movieToRemove);
            return true;
        }
        return false;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }
}
