package com.codesoom.assignment.user.application.port.command;

public interface UserCreateRequest {
    String getEmail();

    String getName();

    String getPassword();
}
