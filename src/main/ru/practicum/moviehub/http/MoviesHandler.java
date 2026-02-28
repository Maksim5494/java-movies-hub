package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class MoviesHandler implements HttpHandler {
    private final MoviesStore moviesStore;
    private final ObjectMapper objectMapper;

    public MoviesHandler(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET") && pathParts.length == 2) {
                handleGetMovies(exchange);
            } else if (method.equals("POST") && pathParts.length == 2) {
                handlePostMovie(exchange);
            } else if (pathParts.length == 3 && pathParts[1].equals("movies")) {
                handleIdRequest(exchange, method, pathParts[2]);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

    private void handleGetMovies(HttpExchange exchange) throws IOException {
        sendResponse(exchange, objectMapper.writeValueAsString(moviesStore.getAllMovies()), 200);
    }

    private void handlePostMovie(HttpExchange exchange) throws IOException {
        try {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            String body = new String(bytes, StandardCharsets.UTF_8);

            Movie movie = objectMapper.readValue(bytes, Movie.class);

            if (movie == null || movie.getTitle() == null || movie.getTitle().isBlank() || movie.getYear() <= 0) {
                sendBadRequest(exchange, "Invalid movie fields: title is required and year must be > 0");
                return;
            }

            moviesStore.addMovie(movie);
            sendResponse(exchange, objectMapper.writeValueAsString(movie), 201);
        } catch (Exception e) {
            sendBadRequest(exchange, "Invalid JSON format");
        }
    }

    private void handleIdRequest(HttpExchange exchange, String method, String idStr) throws IOException {
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid ID format");
            return;
        }

        if (method.equals("GET")) {
            Optional<Movie> movieOpt = moviesStore.getById(id);
            if (movieOpt.isEmpty()) {
                sendNotFound(exchange, "Movie with id " + id + " not found");
            } else {
                sendResponse(exchange, objectMapper.writeValueAsString(movieOpt.get()), 200);
            }
        } else if (method.equals("DELETE")) {
            boolean removed = moviesStore.removeMovieById(id);
            if (removed) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                sendNotFound(exchange, "Movie with id " + id + " not found");
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(400, -1);
    }

    private void sendNotFound(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(404, -1);
    }
}