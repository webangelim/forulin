package com.angelim.dto;

public record NewTopicRequest(String title, String author) {
    public NewTopicRequest {
        if (title == null || title.trim().isEmpty() || title.length() < 5 || title.length() > 100) {
            throw new IllegalArgumentException("O título do tópico deve ter entre 5 e 100 caracteres.");
        }
        if (author == null || author.trim().isEmpty() || author.length() > 50) {
            throw new IllegalArgumentException("O autor deve ter entre 1 e 50 caracteres.");
        }
    }
}