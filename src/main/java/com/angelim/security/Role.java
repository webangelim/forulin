package com.angelim.security;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE, // Acesso Público (Visitantes)
    USER,   // Usuário comum autenticado
    ADMIN   // Administrador com privilégios totais
}