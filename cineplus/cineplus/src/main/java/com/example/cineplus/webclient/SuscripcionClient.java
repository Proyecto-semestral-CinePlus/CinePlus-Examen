package com.example.cineplus.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SuscripcionClient {

    private final WebClient webClient;

    public SuscripcionClient(@Value("${suscripcion-service.url}") String suscripcionServidor) {
        this.webClient = WebClient.builder().baseUrl(suscripcionServidor).build();
    }

    /**
     * Obtiene todas las suscripciones del microservicio ms-suscripciones (puerto 8081).
     * Si el microservicio no responde o devuelve un error, retorna una lista vacía
     * en vez de propagar la excepción (fallback).
     */
    public List<Map<String, Object>> obtenerSuscripciones() {
        try {
            return this.webClient.get()
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .collectList()
                    .block();
        } catch (WebClientRequestException e) {
            return Collections.emptyList();
        } catch (WebClientResponseException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene una suscripción por ID del microservicio ms-suscripciones.
     * Retorna null si el microservicio no responde o la suscripción no existe.
     */
    public Map<String, Object> obtenerSuscripcionId(Long id) {
        try {
            return this.webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .block();
        } catch (WebClientRequestException e) {
            return null;
        } catch (WebClientResponseException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}