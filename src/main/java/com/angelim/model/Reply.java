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
        if (content.length() < 3 || content.length() > 1000) {
            throw new IllegalArgumentException("O conteúdo da resposta deve ter entre 3 e 1000 caracteres.");
        }
        if (content.contains("<") || content.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos no conteúdo.");
        }
        if (author == null || author.trim().isEmpty() || author.length() > 50) {
            throw new IllegalArgumentException("O autor da resposta deve ter entre 1 e 50 caracteres.");
        }
    }
}