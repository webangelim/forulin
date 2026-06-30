package com.angelim.dto;

import com.angelim.security.Role;

public record UserResponse(String id, String username, Role role) {}
