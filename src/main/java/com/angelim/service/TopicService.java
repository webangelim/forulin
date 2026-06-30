package com.angelim.service;

import com.angelim.model.Topic;
import com.angelim.repository.TopicRepository;

import java.util.List;

public class TopicService {

    // O Service precisa conversar com o Banco, então ele recebe o Repository
    private final TopicRepository repository;

    public TopicService(TopicRepository repository) {
        this.repository = repository;
    }

    public List<Topic> getAllTopics() {
        return repository.findAll();
    }

    public Topic getTopicById(String id) {
        return repository.findById(id);
    }

    public Topic createTopic(Topic topic) {
        // REGRA DE NEGÓCIO 1: Unicidade
        // Se o repositório achar um tópico com esse ID, barramos a criação.
        if (repository.findById(topic.id()) != null) {
            // Lançamos uma exceção genérica do Java.
            // O Javalin não está aqui, isso é Java puro!
            throw new IllegalArgumentException("Erro: Um tópico com o ID '" + topic.id() + "' já existe.");
        }

        // Se passou pela regra, mandamos salvar.
        repository.save(topic);
        return topic;
    }

    public void deleteTopic(String id) {
        repository.delete(id);
    }
}