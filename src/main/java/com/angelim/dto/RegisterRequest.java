package com.angelim.dto;

public record RegisterRequest(String username, String password) {
    public RegisterRequest {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }
        if (username.length() < 3 || username.length() > 30) {
            throw new IllegalArgumentException("O nome de usuário deve ter entre 3 e 30 caracteres.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (password.length() < 6 || password.length() > 50) {
            throw new IllegalArgumentException("A senha deve ter entre 6 e 50 caracteres.");
        }
    }
}
