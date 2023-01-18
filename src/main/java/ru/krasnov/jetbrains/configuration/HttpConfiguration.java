package ru.krasnov.jetbrains.configuration;

import lombok.experimental.UtilityClass;

import java.net.http.HttpClient;

@UtilityClass
public class HttpConfiguration {

    public static HttpClient getDefaultHttpClient() {
        return HttpClient.newBuilder().build();
    }
}
