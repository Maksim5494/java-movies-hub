package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import java.io.IOException;
import java.util.List;

public class MoviesHandler extends BaseHttpHandler {
    private MoviesStore moviesStore = new MoviesStore();
    private Gson gson = new Gson();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            List<Movie> movies = moviesStore.getAllMovies();
            String json = gson.toJson(movies);
            sendJson(ex, 200, json);
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }
}
