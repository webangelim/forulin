package com.angelim.service;

import com.angelim.model.User;
import com.angelim.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

public class AuthService {

    // Em produção, isso vira variável de ambiente (System.getenv("JWT_SECRET"))
    private static final String SECRET_KEY = "minha_chave_super_secreta_e_longa";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    // O serviço agora depende do banco de dados
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String authenticate(String username, String plainTextPassword) {
        // 1. Busca o usuário no banco de dados
        User user = userRepository.findByUsername(username);

        // 2. Se o usuário existir, batemos a senha em texto puro contra o hash do banco
        if (user != null && BCrypt.checkpw(plainTextPassword, user.passwordHash())) {
            // 3. Se a senha bater, geramos o token passando o cargo REAL dele no banco
            return generateToken(user.username(), user.role().name());
        }

        // Regra de Ouro de Segurança: Nunca diga "Senha incorreta" ou "Usuário não existe".
        // Diga sempre "Credenciais inválidas" para não dar dicas a hackers.
        throw new IllegalArgumentException("Credenciais inválidas");
    }

    private String generateToken(String username, String role) {
        return JWT.create()
                .withIssuer("ForumAPI")
                .withSubject(username)
                .withClaim("role", role) // Injeta a tag do cargo no token
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))
                .sign(ALGORITHM);
    }

    public DecodedJWT verifyToken(String token) {
        return JWT.require(ALGORITHM)
                .withIssuer("ForumAPI")
                .build()
                .verify(token);
    }
}