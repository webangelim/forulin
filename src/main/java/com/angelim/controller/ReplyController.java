package com.angelim.controller;

import com.angelim.model.Reply;
import com.angelim.service.ReplyService;
import io.javalin.http.Context;

public class ReplyController {

    private final ReplyService service;

    public ReplyController(ReplyService service) {
        this.service = service;
    }

    // GET /api/topics/{topicId}/replies
    public void getRepliesByTopic(Context ctx) {
        String topicId = ctx.pathParam("topicId");
        ctx.json(service.getRepliesByTopic(topicId));
    }

    // POST /api/topics/{topicId}/replies
    public void createReply(Context ctx) {
        String topicId = ctx.pathParam("topicId");

        // Lemos o corpo da requisição
        Reply body = ctx.bodyAsClass(Reply.class);

        // Forçamos o topicId da URL para garantir consistência,
        // mesmo que o usuário envie algo diferente no JSON do Body
        Reply replyToSave = new Reply(body.id(), topicId, body.content(), body.author());

        service.createReply(replyToSave);
        ctx.status(201).json(replyToSave);
    }
}