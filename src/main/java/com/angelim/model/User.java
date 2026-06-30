package com.angelim.model;

import com.angelim.security.Role;

public record User(String id, String username, String passwordHash, Role role) {}
