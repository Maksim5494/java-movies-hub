package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import java.io.IOException;
import java.util.Optional;

public class MoviesHandler extends BaseHttpHandler {
    private final MoviesStore moviesStore;
    private final Gson gson = new Gson();
    private char[] bytes;

    public MoviesHandler(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET") && pathParts.length == 2 && pathParts[1].equals("movies")) {
                handleGetMovies(exchange);
            } else if (method.equals("POST") && pathParts.length == 2 && pathParts[1].equals("movies")) {
                handlePostMovie(exchange);
            } else if (pathParts.length == 3 && pathParts[1].equals("movies")) {
                handleIdRequest(exchange, method, pathParts[2]);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

    private void handleGetMovies(HttpExchange exchange) throws IOException {
        String json = gson.toJson(moviesStore.getAllMovies());
        sendJson(exchange, 200, json);
    }

    private void handlePostMovie(HttpExchange exchange) throws IOException {
        try {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            Movie movie = gson.fromJson(new String(bytes, StandardCharsets.UTF_8), Movie.class);

            if (movie.getTitle() == null || movie.getTitle().isBlank() || movie.getYear() <= 0) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            moviesStore.addMovie(movie);
            sendJson(exchange, 201, gson.toJson(movie));
        } catch (Exception e) {
            exchange.sendResponseHeaders(400, -1);
        }
    }

    private void handleIdRequest(HttpExchange exchange, String method, String idStr) throws IOException {
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        if (method.equals("GET")) {
            Optional<Movie> movieOpt = moviesStore.getById(id);
            if (movieOpt.isEmpty()) {
                exchange.sendResponseHeaders(404, -1);
            } else {
                sendJson(exchange, 200, gson.toJson(movieOpt.get()));
            }
        } else if (method.equals("DELETE")) {
            if (moviesStore.removeMovieById(id)) {
                sendNoContent(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}