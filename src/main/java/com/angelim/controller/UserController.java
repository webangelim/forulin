package com.angelim.controller;

import com.angelim.dto.RegisterRequest;
import com.angelim.dto.UpdateRoleRequest;
import com.angelim.dto.UserResponse;
import com.angelim.service.UserService;
import io.javalin.http.Context;
import io.javalin.openapi.*;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @OpenApi(
            summary = "Cadastrar novo usuário",
            operationId = "registerUser",
            path = "/api/register",
            methods = HttpMethod.POST,
            tags = {"Usuários"},
            requestBody = @OpenApiRequestBody(
                    description = "Dados para cadastro de usuário",
                    content = {@OpenApiContent(from = RegisterRequest.class)},
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "201", description = "Usuário cadastrado com sucesso", content = {@OpenApiContent(from = UserResponse.class)}),
                    @OpenApiResponse(status = "400", description = "Erro de validação (ex: campos vazios, usuário existente)")
            }
    )
    public void register(Context ctx) {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        UserResponse response = userService.register(request);
        ctx.status(201).json(response);
    }

    @OpenApi(
            summary = "Listar todos os usuários",
            operationId = "getAllUsers",
            path = "/api/users",
            methods = HttpMethod.GET,
            tags = {"Usuários"},
            queryParams = {
                    @OpenApiParam(name = "username", type = String.class, description = "Filtro por nome de usuário (busca parcial opcional)", required = false),
                    @OpenApiParam(name = "limit", type = Integer.class, description = "Quantidade de usuários a retornar (padrão: 10, máximo: 50)", required = false),
                    @OpenApiParam(name = "offset", type = Integer.class, description = "Deslocamento de usuários (padrão: 0)", required = false)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Lista de usuários", content = {@OpenApiContent(from = UserResponse[].class)}),
                    @OpenApiResponse(status = "401", description = "Não autorizado (Token ausente)"),
                    @OpenApiResponse(status = "403", description = "Proibido (Privilégios insuficientes)")
            }
    )
    public void getAllUsers(Context ctx) {
        String username = ctx.queryParam("username");
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);

        limit = Math.max(1, Math.min(limit, 50));
        offset = Math.max(0, offset);

        ctx.json(userService.getAllUsers(limit, offset, username));
    }

    @OpenApi(
            summary = "Alterar nível de acesso de um usuário",
            operationId = "updateUserRole",
            path = "/api/users/{id}/role",
            methods = HttpMethod.PATCH,
            tags = {"Usuários"},
            pathParams = {
                    @OpenApiParam(name = "id", description = "ID do usuário a ter a role alterada", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    description = "Novo nível de acesso (Role)",
                    content = {@OpenApiContent(from = UpdateRoleRequest.class)},
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Role atualizada com sucesso", content = {@OpenApiContent(from = UserResponse.class)}),
                    @OpenApiResponse(status = "400", description = "Dados inválidos"),
                    @OpenApiResponse(status = "401", description = "Não autorizado (Token ausente)"),
                    @OpenApiResponse(status = "403", description = "Proibido (Privilégios insuficientes)"),
                    @OpenApiResponse(status = "404", description = "Usuário não encontrado")
            }
    )
    public void updateUserRole(Context ctx) {
        String id = ctx.pathParam("id");
        UpdateRoleRequest request = ctx.bodyAsClass(UpdateRoleRequest.class);
        UserResponse response = userService.updateUserRole(id, request.role());
        ctx.json(response);
    }
}
