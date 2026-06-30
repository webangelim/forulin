package com.angelim.controller;

import com.angelim.dto.LoginRequest;
import com.angelim.dto.TokenResponse;
import com.angelim.service.AuthService;
import io.javalin.http.Context;
import io.javalin.openapi.*;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @OpenApi(
            summary = "Realizar Login",
            description = "Recebe usuário e senha e retorna um token JWT caso as credenciais sejam válidas.",
            operationId = "login",
            path = "/api/login",
            methods = HttpMethod.POST,
            tags = {"Autenticação"},
            requestBody = @OpenApiRequestBody(
                    description = "Credenciais de acesso",
                    content = {@OpenApiContent(from = LoginRequest.class)},
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Login bem-sucedido", content = {@OpenApiContent(from = TokenResponse.class)}),
                    @OpenApiResponse(status = "401", description = "Credenciais inválidas")
            }
    )
    public void login(Context ctx) {
        LoginRequest req = ctx.bodyAsClass(LoginRequest.class);

        try {
            // O serviço vai buscar no banco, bater o BCrypt e gerar o JWT
            String token = authService.authenticate(req.username(), req.password());

            // Retornamos um JSON padronizado com o token dentro
            ctx.json(new TokenResponse(token));

        } catch (IllegalArgumentException e) {
            // Captura o "Credenciais inválidas" lançado pelo AuthService
            ctx.status(401).result(e.getMessage());
        }
    }
}
