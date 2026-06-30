package com.angelim.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.time.Duration;

public class RateLimiter {

    // Mapeia uma chave (ex: IP do cliente) para uma fila concorrente de timestamps das requisições recentes
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Instant>> requestTimestamps = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxRequests, Duration window) {
        Instant now = Instant.now();
        Instant limitTime = now.minus(window);

        // Obtém ou cria a fila de timestamps para a chave especificada
        ConcurrentLinkedQueue<Instant> timestamps = requestTimestamps.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());

        // Remove os timestamps que estão fora da janela de tempo atual (mais antigos que o limite)
        while (!timestamps.isEmpty() && timestamps.peek().isBefore(limitTime)) {
            timestamps.poll();
        }

        // Se o número de requisições recentes estiver abaixo do máximo permitido, registra e permite o acesso
        if (timestamps.size() < maxRequests) {
            timestamps.add(now);
            return true;
        }

        return false;
    }
}
