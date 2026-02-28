package ru.practicum.moviehub.http;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.model.Movie;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesHandler extends BaseHttpHandler {
    private final MoviesStore moviesStore;
    private final Gson gson = new Gson();

    public MoviesHandler(MoviesStore moviesStore) {
        this.moviesStore = moviesStore;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            List<Movie> movies = moviesStore.getAllMovies();
            sendJson(ex, 200, gson.toJson(movies));
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
        String contentType = ex.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.contains("application/json")) {
            ex.sendResponseHeaders(415, -1);
            return;
        }

        String requestBody = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Movie newMovie = gson.fromJson(requestBody, Movie.class);

        List<String> validationErrors = validateMovie(newMovie);
        if (!validationErrors.isEmpty()) {
            String errorMessage = gson.toJson(new ErrorResponse(422, validationErrors.toString()));
            sendJson(ex, 422, errorMessage);
            return;
        }

        moviesStore.addMovie(newMovie);
        sendNoContent(ex); //
    }

    private List<String> validateMovie(Movie movie) {
        List<String> errors = new ArrayList<>();
        if (movie.getTitle() == null || movie.getTitle().isEmpty() || movie.getTitle().length() > 100) {
            errors.add("название не должно быть пустым и до 100 символов");
        }
        int currentYear = LocalDate.now().getYear();
        if (movie.getYear() < 1888 || movie.getYear() > currentYear + 1) {
            errors.add("год должен быть между 1888 и " + (currentYear + 1));
        }
        return errors;
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
        String path = ex.getRequestURI().getPath();
        String idStr = path.substring(path.lastIndexOf("/") + 1);
        try {
            long id = Long.parseLong(idStr);
            if (moviesStore.removeMovieById(id)) {
                sendNoContent(ex);
            } else {
                ex.sendResponseHeaders(404, -1);
            }
        } catch (NumberFormatException e) {
            ex.sendResponseHeaders(400, -1);
        }
    }
}