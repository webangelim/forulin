package com.angelim.controller;

import com.angelim.dto.NewReplyRequest;
import com.angelim.model.Reply;
import com.angelim.service.ReplyService;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import java.util.UUID;

public class ReplyController {

    private final ReplyService service;

    public ReplyController(ReplyService service) {
        this.service = service;
    }

    // GET /api/topics/{topicId}/replies
    @OpenApi(
            summary = "Buscar respostas de um tópico",
            operationId = "getRepliesByTopic",
            path = "/api/topics/{topicId}/replies",
            methods = HttpMethod.GET,
            tags = {"Respostas"},
            pathParams = {
                    @OpenApiParam(name = "topicId", description = "ID do tópico do qual buscar as respostas", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Reply[].class)})
            }
    )
    public void getRepliesByTopic(Context ctx) {
        String topicId = ctx.pathParam("topicId");
        ctx.json(service.getRepliesByTopic(topicId));
    }

    // POST /api/topics/{topicId}/replies
    @OpenApi(
            summary = "Criar uma resposta em um tópico",
            operationId = "createReply",
            path = "/api/topics/{topicId}/replies",
            methods = HttpMethod.POST,
            tags = {"Respostas"},
            pathParams = {
                    @OpenApiParam(name = "topicId", description = "ID do tópico ao qual adicionar a resposta", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    description = "Dados da nova resposta",
                    content = {@OpenApiContent(from = NewReplyRequest.class)},
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "201", description = "Resposta criada com sucesso", content = {@OpenApiContent(from = Reply.class)}),
                    @OpenApiResponse(status = "400", description = "Erro de validação (ex: conteúdo vazio)"),
                    @OpenApiResponse(status = "401", description = "Não autorizado (Token ausente)")
            }
    )
    public void createReply(Context ctx) {
        String topicId = ctx.pathParam("topicId");

        // Lemos o corpo da requisição usando o DTO
        NewReplyRequest request = ctx.bodyAsClass(NewReplyRequest.class);

        // O Servidor gera o UUID para a resposta
        String generatedId = UUID.randomUUID().toString();

        // Monta a entidade real
        Reply replyToSave = new Reply(generatedId, topicId, request.content(), request.author());

        service.createReply(replyToSave);
        ctx.status(201).json(replyToSave);
    }
}