package com.example.cineplus.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SuscripcionClient {

    private final WebClient webClient;

    public SuscripcionClient(@Value("${suscripcion-service.url}") String suscripcionServidor) {
        this.webClient = WebClient.builder().baseUrl(suscripcionServidor).build();
    }

    public List<Map<String, Object>> obtenerSuscripciones() {
        log.info("Consultando suscripciones desde ms-suscripciones");
        try {
            return this.webClient.get()
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .collectList()
                    .block();
        } catch (WebClientRequestException e) {
            log.error("ms-suscripciones no disponible: {}", e.getMessage());
            return Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ms-suscripciones respondió con error: {}", e.getStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error inesperado al consultar ms-suscripciones: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> obtenerSuscripcionId(Long id) {
        log.info("Consultando suscripción con id: {} desde ms-suscripciones", id);
        try {
            return this.webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .block();
        } catch (WebClientRequestException e) {
            log.error("ms-suscripciones no disponible: {}", e.getMessage());
            return null;
        } catch (WebClientResponseException e) {
            log.error("ms-suscripciones respondió con error: {}", e.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error inesperado al consultar ms-suscripciones: {}", e.getMessage());
            return null;
        }
    }
}