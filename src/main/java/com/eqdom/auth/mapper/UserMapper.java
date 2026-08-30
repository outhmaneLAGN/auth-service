package com.eqdom.auth.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eqdom.auth.dto.UserResponse;
import com.eqdom.auth.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .cin(user.getCin())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
