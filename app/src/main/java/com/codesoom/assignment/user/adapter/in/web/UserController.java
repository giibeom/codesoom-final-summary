package com.codesoom.assignment.user.adapter.in.web;

import com.codesoom.assignment.common.security.UserAuthentication;
import com.codesoom.assignment.user.adapter.in.web.dto.request.UserCreateRequestDto;
import com.codesoom.assignment.user.adapter.in.web.dto.request.UserUpdateRequestDto;
import com.codesoom.assignment.user.adapter.in.web.dto.response.CreateUserResponseDto;
import com.codesoom.assignment.user.adapter.in.web.dto.response.UpdateUserResponseDto;
import com.codesoom.assignment.user.application.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateUserResponseDto create(@RequestBody @Valid UserCreateRequestDto registrationData) {
        return new CreateUserResponseDto(
                userService.registerUser(registrationData)
        );
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    UpdateUserResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDto modificationData,
            UserAuthentication authentication
    ) throws AccessDeniedException {
        return new UpdateUserResponseDto(
                userService.updateUser(id, modificationData, authentication.getUserId())
        );
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    void destroy(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
