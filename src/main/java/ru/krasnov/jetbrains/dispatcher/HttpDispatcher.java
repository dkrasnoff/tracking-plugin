package ru.krasnov.jetbrains.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gradle.internal.impldep.org.apache.http.HttpStatus;
import org.mapstruct.factory.Mappers;
import ru.krasnov.jetbrains.extension.PluginParametersExtension;
import ru.krasnov.jetbrains.mapper.BuildResultsMapper;
import ru.krasnov.jetbrains.model.BuildTracker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpDispatcher implements Dispatcher {

    private final static String DEFAULT_RESULT_COLLECTOR_SERVICE_URL = "http://localhost:8080/build";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final PluginParametersExtension pluginParameters;
    private final BuildResultsMapper buildResultsMapper;

    public HttpDispatcher(ObjectMapper objectMapper,
                          HttpClient httpClient,
                          PluginParametersExtension pluginParameters) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.pluginParameters = pluginParameters;
        this.buildResultsMapper = Mappers.getMapper(BuildResultsMapper.class);
    }

    @Override
    public void sendExecutedBuildResult(BuildTracker buildTracker) {

        try {

            final var body = objectMapper.writeValueAsString(
                    buildResultsMapper.mapFromModel(buildTracker));
            final var resultCollectingEndpoint = pluginParameters.getResultsCollectorService()
                    .getUrl()
                    .getOrElse(DEFAULT_RESULT_COLLECTOR_SERVICE_URL);

            log.info("Endpoint for sending build results is : " + resultCollectingEndpoint);
            final var request = HttpRequest.newBuilder()
                    .uri(new URI(resultCollectingEndpoint))
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
