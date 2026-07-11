package com.example.cineplus.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class SuscripcionClient {

    private final WebClient webClient;

    public SuscripcionClient(@Value("${suscripcion-service.url}") String suscripcionServidor) {
        this.webClient = WebClient.builder().baseUrl(suscripcionServidor).build();
    }

    // metodo para obtener todas las suscripciones del microservicio
    public List<Map<String, Object>> obtenerSuscripciones() {
        return this.webClient.get()
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error al obtener suscripciones")))
                .bodyToFlux(Map.class)
                .cast(Map.class)
                .map(m -> (Map<String, Object>) m)
                .collectList()
                .block();
    }

    // metodo para obtener una suscripcion por id del microservicio
    public Map<String, Object> obtenerSuscripcionId(Long id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Suscripción no encontrada")))
                .bodyToMono(Map.class)
                .cast(Map.class)
                .map(m -> (Map<String, Object>) m)
                .block();
    }
}