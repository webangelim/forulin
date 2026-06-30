package com.angelim.dto;

import com.angelim.security.Role;

public record UpdateRoleRequest(Role role) {
    public UpdateRoleRequest {
        if (role == null) {
            throw new IllegalArgumentException("A role é obrigatória.");
        }
    }
}
