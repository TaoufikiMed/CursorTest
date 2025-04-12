package com.example.secureapi.dto;

import com.example.secureapi.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private boolean active;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.active = user.isActive();
    }
} 