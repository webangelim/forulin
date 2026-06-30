package com.angelim.service;

import com.angelim.dto.RegisterRequest;
import com.angelim.dto.UserResponse;
import com.angelim.model.User;
import com.angelim.repository.UserRepository;
import com.angelim.security.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(RegisterRequest request) {
        // Valida se o usuário já existe
        if (userRepository.findByUsername(request.username()) != null) {
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }

        // Criptografa a senha com BCrypt
        String passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        String generatedId = UUID.randomUUID().toString();

        // Salva com a role padrão USER
        User newUser = new User(generatedId, request.username(), passwordHash, Role.USER);
        userRepository.save(newUser);

        return new UserResponse(newUser.id(), newUser.username(), newUser.role());
    }

    public List<UserResponse> getAllUsers(int limit, int offset) {
        return userRepository.findAll(limit, offset).stream()
                .map(user -> new UserResponse(user.id(), user.username(), user.role()))
                .collect(Collectors.toList());
    }

    public UserResponse updateUserRole(String id, Role newRole) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        userRepository.updateRole(id, newRole);

        return new UserResponse(user.id(), user.username(), newRole);
    }
}
