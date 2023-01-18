package ru.krasnov.jetbrains.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gradle.internal.impldep.org.apache.http.HttpStatus;
import ru.krasnov.jetbrains.model.BuildTracker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpDispatcher implements Dispatcher {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HttpDispatcher(ObjectMapper objectMapper,
                          HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public void sendExecutedBuildResult(BuildTracker buildTracker) {

        try {
            final var body = objectMapper.writeValueAsString(buildTracker);
            final var request = HttpRequest.newBuilder()
                    // TODO(d.krasnov): вынести в параметр
                    .uri(new URI("http://localhost:8080/build"))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.SC_CREATED) {
                log.error(String.format("Build result sending failed with %s code response", response.statusCode()));
            }

        } catch (Exception e) {
            log.error("Unexpected exception during sending build results to server", e);
        }
    }
}
