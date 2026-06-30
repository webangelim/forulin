package com.angelim.dto;

public record NewReplyRequest(String content, String author) {
    public NewReplyRequest {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo da resposta é obrigatório.");
        }
        if (content.length() < 3 || content.length() > 1000) {
            throw new IllegalArgumentException("O conteúdo deve ter entre 3 e 1000 caracteres.");
        }
        if (content.contains("<") || content.contains(">")) {
            throw new IllegalArgumentException("Caracteres HTML/Scripts não são permitidos.");
        }
        if (author == null || author.trim().isEmpty() || author.length() > 50) {
            throw new IllegalArgumentException("O autor da resposta deve ter entre 1 e 50 caracteres.");
        }
    }
}
