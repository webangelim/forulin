package com.angelim;

import com.angelim.controller.ReplyController;
import com.angelim.controller.TopicController;
import com.angelim.repository.ReplyRepository;
import com.angelim.repository.TopicRepository;
import com.angelim.service.ReplyService;
import com.angelim.service.TopicService;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import org.jdbi.v3.core.Jdbi;

public class Main {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:forum.db";
        Jdbi jdbi = Jdbi.create(dbUrl);

        TopicRepository topicRepository = new TopicRepository(jdbi);
        ReplyRepository replyRepository = new ReplyRepository(jdbi);

        TopicService topicService = new TopicService(topicRepository);
        ReplyService replyService = new ReplyService(replyRepository, topicRepository);

        TopicController topicController = new TopicController(topicService);
        ReplyController replyController = new ReplyController(replyService);

        var app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(openApiConfig -> {
                openApiConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.info(info -> {
                        info.title("API do Fórum");
                        info.version("1.0.0");
                    });
                });
            }));

            config.registerPlugin(new SwaggerPlugin(swaggerConfig -> {
                swaggerConfig.withUiPath("/docs");
            }));

            config.routes.get("/", ctx -> ctx.result("Hello World"))
                    .get("/api/topics", topicController::getAllTopics)
                    .get("/api/topics/{id}", topicController::getTopicById)
                    .post("/api/topics", topicController::createTopic)
                    .delete("/api/topics/{id}", topicController::deleteTopic)
                    .get("/api/topics/{topicId}/replies", replyController::getRepliesByTopic)
                    .post("/api/topics/{topicId}/replies", replyController::createReply);
        }).start(7070);
    }
}