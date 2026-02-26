package ru.practicum.moviehub.http;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.model.Movie;
import java.nio.charset.StandardCharsets;

public class MoviesHandler implements HttpHandler {
    private final MoviesStore moviesStore;
    private Gson gson = new Gson();

    public MoviesHandler(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            // Логика для GET
        } else if (method.equalsIgnoreCase("POST")) {
            handlePost(ex);
        } else if (method.equalsIgnoreCase("PUT")) {
            handlePut(ex);
        } else if (method.equalsIgnoreCase("DELETE")) {
            handleDelete(ex);
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }

    public void handlePost(HttpExchange ex) throws IOException {
        String requestBody = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie newMovie = gson.fromJson(requestBody, Movie.class);
        moviesStore.addMovie(newMovie);
        ex.sendResponseHeaders(201, -1);
    }

    public void handlePut(HttpExchange ex) throws IOException {
        String requestBody = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie updatedMovie = gson.fromJson(requestBody, Movie.class);
        if (moviesStore.updateMovie(updatedMovie)) {
            ex.sendResponseHeaders(200, -1);
        } else {
            ex.sendResponseHeaders(404, -1);
        }
    }

    public void handleDelete(HttpExchange ex) throws IOException {
        String movieId = ex.getRequestURI().getPath().replace("/movies/", "");
        if (moviesStore.removeMovieById(Long.parseLong(movieId))) {
            ex.sendResponseHeaders(204, -1);
        } else {
            ex.sendResponseHeaders(404, -1);
        }
    }

}