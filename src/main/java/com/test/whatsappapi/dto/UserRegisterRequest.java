package com.test.whatsappapi.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String displayName;
    private String email;
    private String password;
    private String profilePicture;
    private String about;
}