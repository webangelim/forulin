package com.angelim.model;

public record Reply(String id, String topicId, String content, String author) {

    public Reply {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da resposta é obrigatório.");
        }
        if (topicId == null || topicId.trim().isEmpty()) {
            throw new IllegalArgumentException("A resposta deve estar vinculada a um tópico.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo da resposta não pode estar vazio.");
        }
        if (content.contains("<") || content.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos no conteúdo.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("O autor da resposta é obrigatório.");
        }
    }
}