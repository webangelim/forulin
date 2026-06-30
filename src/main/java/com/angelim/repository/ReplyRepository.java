package com.angelim.repository;

import com.angelim.model.Reply;
import org.jdbi.v3.core.Jdbi;
import java.util.List;

public class ReplyRepository {

    private final Jdbi jdbi;

    public ReplyRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        jdbi.useHandle(handle -> {
            handle.execute("""
                CREATE TABLE IF NOT EXISTS replies (
                    id TEXT PRIMARY KEY,
                    topic_id TEXT NOT NULL,
                    content TEXT NOT NULL,
                    author TEXT NOT NULL,
                    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
                )
            """);
        });
    }

    // Buscar todas as respostas de um tópico específico
    public List<Reply> findByTopicId(String topicId) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM replies WHERE topic_id = :topicId")
                        .bind("topicId", topicId)
                        .map((rs, ctx) -> new Reply(
                                rs.getString("id"),
                                rs.getString("topic_id"),
                                rs.getString("content"),
                                rs.getString("author")
                        ))
                        .list()
        );
    }

    public void save(Reply reply) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("""
                INSERT INTO replies (id, topic_id, content, author)
                VALUES (:id, :topic_id, :content, :author)
            """)
                    .bind("id", reply.id())
                    .bind("topic_id", reply.topicId())
                    .bind("content", reply.content())
                    .bind("author", reply.author())
                    .execute();
        });
    }
}
