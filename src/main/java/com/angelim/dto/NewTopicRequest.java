package com.angelim.dto;

public record NewTopicRequest(String title, String author) {
    public NewTopicRequest {
        if (title == null || title.trim().isEmpty() || title.length() < 5) {
            throw new IllegalArgumentException("Título inválido.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor inválido.");
        }
    }
}