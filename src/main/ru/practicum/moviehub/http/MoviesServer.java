package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

class MoviesServer {
    private final HttpServer server;

    public MoviesServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        // Добавляем маршрут для /movies и обработчик
        server.createContext("/movies", new MoviesHandler());
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    // Обработчик для маршрута /movies
    private class MoviesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Ваш код обработки запроса
            String response = "[]"; // Пример ответа в формате JSON
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}