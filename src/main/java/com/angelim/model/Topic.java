package com.angelim.model;

public record Topic(String id, String title, String author) {
    public Topic {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode ser vazio");
        }
        if (title.length() < 5 || title.length() > 100) {
            throw new IllegalArgumentException("O título deve ter entre 5 e 100 caracteres");
        }
        if (title.contains("<") || title.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos");
        }

        // Podemos validar o autor também!
        if (author == null || author.trim().isEmpty() || author.length() > 50) {
            throw new IllegalArgumentException("O autor deve ter entre 1 e 50 caracteres");
        }
    }
}
