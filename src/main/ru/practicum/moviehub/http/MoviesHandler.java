package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import java.io.IOException;
import java.util.List;

public class MoviesHandler extends BaseHttpHandler {
    private MoviesStore moviesStore = new MoviesStore();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            List<Movie> movies = moviesStore.getAllMovies();
            JSONArray jsonArray = new JSONArray();
            for (Movie movie : movies) {
                JSONObject jsonMovie = new JSONObject();
                jsonMovie.put("title", movie.getTitle());
                jsonMovie.put("year", movie.getYear());
                jsonMovie.put("genre", movie.getGenre());
                jsonMovie.put("rating", movie.getRating());
                jsonArray.put(jsonMovie);
            }
            String json = jsonArray.toString();
            sendJson(ex, 200, json);
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }
}
