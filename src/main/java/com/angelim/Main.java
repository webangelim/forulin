package com.angelim;

import com.angelim.controller.AuthController;
import com.angelim.controller.ReplyController;
import com.angelim.controller.TopicController;
import com.angelim.repository.ReplyRepository;
import com.angelim.repository.TopicRepository;
import com.angelim.repository.UserRepository;
import com.angelim.security.Role;
import com.angelim.service.AuthService;
import com.angelim.service.ReplyService;
import com.angelim.service.TopicService;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import com.angelim.security.RateLimiter;
import com.angelim.security.TooManyRequestsException;
import org.jdbi.v3.core.Jdbi;

public class Main {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:forum.db";
        Jdbi jdbi = Jdbi.create(dbUrl);

        TopicRepository topicRepository = new TopicRepository(jdbi);
        ReplyRepository replyRepository = new ReplyRepository(jdbi);
        UserRepository userRepository = new UserRepository(jdbi);

        TopicService topicService = new TopicService(topicRepository);
        AuthService authService = new AuthService(userRepository);
        ReplyService replyService = new ReplyService(replyRepository, topicRepository);

        TopicController topicController = new TopicController(topicService);
        ReplyController replyController = new ReplyController(replyService);
        AuthController authController = new AuthController(authService);

        RateLimiter rateLimiter = new RateLimiter();

        var app = Javalin.create(config -> {
            config.routes.beforeMatched(ctx -> {
                var routeRoles = ctx.routeRoles();

                if (routeRoles.isEmpty() || routeRoles.contains(Role.ANYONE)) {
                    return;
                }

                String authHeader = ctx.header("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new io.javalin.http.UnauthorizedResponse("Acesso negado: Token ausente.");
                }

                String token = authHeader.substring(7);

                try {
                    // MÁGICA REAL: Validamos o token de verdade
                    var decodedJwt = authService.verifyToken(token);

                    // Lemos qual é o cargo que o servidor assinou dentro do token na hora do login
                    Role userRole = Role.valueOf(decodedJwt.getClaim("role").asString());

                    if (!routeRoles.contains(userRole)) {
                        throw new io.javalin.http.ForbiddenResponse("Acesso negado: Privilégios insuficientes.");
                    }

                    // Opcional: Injetamos quem é o dono do token para usar no TopicController
                    ctx.attribute("usuarioLogado", decodedJwt.getSubject());

                } catch (io.javalin.http.ForbiddenResponse e) {
                    throw e;
                } catch (Exception e) {
                    throw new io.javalin.http.UnauthorizedResponse("Token inválido ou adulterado.");
                }
            });
            config.registerPlugin(new OpenApiPlugin(openApiConfig -> {
                openApiConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.info(info -> {
                        info.title("API do Fórum");
                        info.version("1.0.0");
                    });
                    definition.withBearerAuth();
                    definition.withGlobalSecurity("BearerAuth");
                });
            }));

            config.registerPlugin(new SwaggerPlugin(swaggerConfig -> {
                swaggerConfig.withUiPath("/docs");
            }));

            config.routes.before("/api/login", ctx -> {
                        String ip = ctx.ip();
                        if (!rateLimiter.isAllowed("login:" + ip, 5, java.time.Duration.ofMinutes(1))) {
                            throw new TooManyRequestsException("Muitas tentativas de login. Tente novamente em 1 minuto.");
                        }
                    })
                    .get("/", ctx -> ctx.result("Hello World"))
                    .post("/api/login", authController::login)
                    .get("/api/topics", topicController::getAllTopics, Role.ANYONE)
                    .get("/api/topics/{id}", topicController::getTopicById, Role.ANYONE)
                    .post("/api/topics", topicController::createTopic, Role.USER)
                    .delete("/api/topics/{id}", topicController::deleteTopic, Role.ADMIN)
                    .get("/api/topics/{topicId}/replies", replyController::getRepliesByTopic, Role.ANYONE)
                    .post("/api/topics/{topicId}/replies", replyController::createReply, Role.USER);

            config.routes.exception(IllegalArgumentException.class, (e, ctx) -> {
                ctx.status(400).json(java.util.Map.of("error", e.getMessage()));
            });

            config.routes.exception(com.fasterxml.jackson.core.JsonProcessingException.class, (e, ctx) -> {
                ctx.status(400).json(java.util.Map.of("error", "JSON malformado, vazio ou com formato inválido."));
            });

            config.routes.exception(TooManyRequestsException.class, (e, ctx) -> {
                ctx.status(429).json(java.util.Map.of("error", e.getMessage()));
            });
        }).start(7070);

    }
}