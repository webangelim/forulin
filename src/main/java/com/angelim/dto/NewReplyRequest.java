package com.angelim.dto;

public record NewReplyRequest(String content, String author) {
    public NewReplyRequest {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo da resposta é obrigatório.");
        }
        if (content.length() < 3) {
            throw new IllegalArgumentException("O conteúdo deve ter no mínimo 3 caracteres.");
        }
        if (content.contains("<") || content.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("O autor da resposta é obrigatório.");
        }
    }
}
