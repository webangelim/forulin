package com.angelim.model;

public record Topic(String id, String title, String author) {
    public Topic {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode ser vazio");
        }
        if (title.length() < 5) {
            throw new IllegalArgumentException("O título deve ter no mínimo 5 caracteres");
        }
        if (title.contains("<") || title.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos");
        }

        // Podemos validar o autor também!
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("O autor é obrigatório");
        }
    }
}
