package com.angelim.repository;

import com.angelim.model.Topic;

import java.util.List;
import org.jdbi.v3.core.Jdbi;

public class TopicRepository {

    // Em vez do HashMap, agora dependemos da instância do JDBI
    private final Jdbi jdbi;

    public TopicRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
        // Criar a tabela automaticamente se ela não existir ao iniciar a app
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        jdbi.useHandle(handle -> {
            handle.execute("""
                CREATE TABLE IF NOT EXISTS topics (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL
                )
            """);
        });
    }

    public List<Topic> findAll(int limit, int offset) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM topics LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        // Mapeamos manualmente cada linha do banco (ResultSet) para o nosso Record Topic
                        // Isso tira toda a "mágica" e te mostra como o Java reconstrói os objetos
                        .map((rs, ctx) -> new Topic(
                                rs.getString("id"),
                                rs.getString("title"),
                                rs.getString("author")
                        ))
                        .list()
        );
    }

    public Topic findById(String id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM topics WHERE id = :id")
                        .bind("id", id) // O JDBI limpa o parâmetro contra SQL Injection automaticamente!
                        .map((rs, ctx) -> new Topic(
                                rs.getString("id"),
                                rs.getString("title"),
                                rs.getString("author")
                        ))
                        .findOne() // Retorna um Optional<Topic>
                        .orElse(null)
        );
    }

    public void save(Topic topic) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("""
                INSERT INTO topics (id, title, author) 
                VALUES (:id, :title, :author)
            """)
                    .bind("id", topic.id())
                    .bind("title", topic.title())
                    .bind("author", topic.author())
                    .execute();
        });
    }

    public void delete(String id) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("DELETE FROM topics WHERE id = :id")
                    .bind("id", id)
                    .execute();
        });
    }
}