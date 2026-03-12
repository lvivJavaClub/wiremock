package ua.javaclub.lviv.wiremock;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class UsersApiClient {
    private final HttpClient http;
    private final String baseUrl;

    public UsersApiClient(String baseUrl) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.baseUrl = baseUrl;
    }

    public String getUserJson(int id) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/" + id))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return resp.body();
            }
            throw new RuntimeException("Non-2xx response: " + resp.statusCode() + ", body=" + resp.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP call failed", e);
        }
    }

    public String getUserJsonPathRaw(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return resp.body();
            throw new RuntimeException("Non-2xx response: " + resp.statusCode());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
