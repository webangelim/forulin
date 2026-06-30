package com.angelim.dto;

public record RegisterRequest(String username, String password) {
    public RegisterRequest {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("O nome de usuário deve ter pelo menos 3 caracteres.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
    }
}
