package com.angelim.controller;

import com.angelim.dto.NewTopicRequest;
import com.angelim.model.Topic;
import com.angelim.service.TopicService;
import io.javalin.http.Context;
import io.javalin.openapi.*;

import java.util.UUID;

public class TopicController {

    // O Controller agora depende do Service, não do Repository!
    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @OpenApi(
            summary = "Buscar todos os tópicos",
            operationId = "getAllTopics",
            path = "/api/topics",
            methods = HttpMethod.GET,
            tags = {"Tópicos"},
            responses = {
                    // Diz ao Swagger que retorna status 200 e uma lista de Topics
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Topic[].class)})
            }
    )
    public void getAllTopics(Context ctx) {
        ctx.json(service.getAllTopics());
    }

    @OpenApi(
            summary = "Buscar tópico por id",
            operationId = "getTopicById",
            path = "/api/topics/{id}",
            methods = HttpMethod.GET,
            tags = {"Tópicos"},
            responses = {
                    // 1. Retorna apenas UM Topic, então tiramos o []
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Topic.class)}),
                    // 2. Documentando o que acontece se não achar
                    @OpenApiResponse(status = "404", description = "Tópico não encontrado")
            }
    )
    public void getTopicById(Context ctx) {
        String id = ctx.pathParam("id");
        Topic topic = service.getTopicById(id);

        if (topic != null) {
            ctx.json(topic);
        } else {
            ctx.status(404).result("Tópico não encontrado");
        }
    }

    @OpenApi(
            summary = "Cria um novo tópico",
            operationId = "createTopic",
            path = "/api/topics",
            methods = HttpMethod.POST,
            tags = {"Tópicos"},
            // 1. Isso cria a caixa de texto no Swagger pedindo um JSON no formato de Topic
            requestBody = @OpenApiRequestBody(
                    description = "Dados do novo tópico",
                    content = {@OpenApiContent(from = NewTopicRequest.class)},
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "201", description = "Tópico criado com sucesso", content = {@OpenApiContent(from = Topic.class)}),
                    @OpenApiResponse(status = "400", description = "Erro de validação (ex: título vazio ou ID duplicado)")
            }
    )
    public void createTopic(Context ctx) {
        // 1. Recebe APENAS título e autor
        NewTopicRequest request = ctx.bodyAsClass(NewTopicRequest.class);

        // 2. O Servidor gera o UUID
        String generatedId = UUID.randomUUID().toString();

        // 3. Monta a entidade real
        Topic newTopic = new Topic(generatedId, request.title(), request.author());

        // 4. Salva e retorna
        service.createTopic(newTopic);
        ctx.status(201).json(newTopic);
    }

    @OpenApi(
            summary = "Deleta um tópico",
            operationId = "deleteTopic",
            path = "/api/topics/{id}",
            methods = HttpMethod.DELETE,
            tags = {"Tópicos"},
            pathParams = {
                    @OpenApiParam(name = "id", description = "ID do tópico a ser removido", required = true)
            },
            responses = {
                    // Retorno 204 não tem "content" porque o body é vazio
                    @OpenApiResponse(status = "204", description = "Tópico deletado com sucesso"),
                    @OpenApiResponse(status = "401", description = "Não autorizado (Token ausente)")
            }
    )
    public void deleteTopic(Context ctx) {
        String id = ctx.pathParam("id");
        service.deleteTopic(id);
        ctx.status(204);
    }
}