package com.codesoom.assignment.user.application.port.command;

import com.codesoom.assignment.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserUpdateRequest {
    String getName();

    String getPassword();

    User toEntity(final PasswordEncoder passwordEncoder);
}
