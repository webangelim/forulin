package com.angelim.repository;

import com.angelim.model.User;
import com.angelim.security.Role;
import org.jdbi.v3.core.Jdbi;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserRepository {

    private final Jdbi jdbi;

    public UserRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
        createTableAndSeedAdmin();
    }

    private void createTableAndSeedAdmin() {
        jdbi.useHandle(handle -> {
            // Cria a tabela de usuários
            handle.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            """);

            // Verifica se a tabela está vazia
            int count = handle.createQuery("SELECT count(*) FROM users")
                    .mapTo(Integer.class)
                    .one();

            // Se estiver vazia, cria o primeiro admin do fórum
            if (count == 0) {
                // BCrypt.gensalt() é essencial aqui: ele garante que senhas iguais tenham hashes diferentes
                String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt());

                handle.createUpdate("INSERT INTO users (id, username, password_hash, role) VALUES (:id, :username, :hash, :role)")
                        .bind("id", UUID.randomUUID().toString())
                        .bind("username", "admin")
                        .bind("hash", adminHash)
                        .bind("role", Role.ADMIN.name())
                        .execute();
            }
        });
    }

    // Metodo que o AuthService vai usar para buscar o cara pelo login
    public User findByUsername(String username) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE username = :username")
                        .bind("username", username)
                        .map((rs, ctx) -> new User(
                                rs.getString("id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                Role.valueOf(rs.getString("role")) // Converte a String do banco de volta para o Enum
                        ))
                        .findOne()
                        .orElse(null)
        );
    }
}
