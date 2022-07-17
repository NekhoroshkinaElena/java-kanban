package http;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private String apiToken;
    private final URL url;
    HttpClient client = HttpClient.newHttpClient();

    public KVClient(URL url) {
        this.url = url;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + KVPaths.REGISTER))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                throw new EmptyApiTokenException("api token not set");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + KVPaths.LOAD + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        String result = "";

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result = response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void put(String key, String value) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + KVPaths.SAVE + key + "?API_TOKEN=" + apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Ошибка отправки");
                System.out.println("Код ответа: " + response.statusCode());
                System.out.println("Тело ответа: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
