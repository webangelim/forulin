package com.angelim.service;

import com.angelim.model.Reply;
import com.angelim.repository.ReplyRepository;
import com.angelim.repository.TopicRepository;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository;
    private final TopicRepository topicRepository; // Necessário para validação de negócio

    public ReplyService(ReplyRepository replyRepository, TopicRepository topicRepository) {
        this.replyRepository = replyRepository;
        this.topicRepository = topicRepository;
    }

    public List<Reply> getRepliesByTopic(String topicId, int limit, int offset) {
        // Verifica se o tópico existe antes de buscar as respostas
        if (topicRepository.findById(topicId) == null) {
            throw new IllegalArgumentException("Erro: O tópico informado não existe.");
        }
        return replyRepository.findByTopicId(topicId, limit, offset);
    }

    public Reply createReply(Reply reply) {
        // REGRA DE NEGÓCIO: Garantir integridade antes do banco disparar erro
        if (topicRepository.findById(reply.topicId()) == null) {
            throw new IllegalArgumentException("Erro: Impossível responder. O tópico '" + reply.topicId() + "' não existe.");
        }

        replyRepository.save(reply);
        return reply;
    }
}
