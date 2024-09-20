package com.sparta.outsouringproject.user.dto;

import lombok.Data;

@Data
public class UserRequestDto {

    private String id;
    private String username;
    private String password;
    private String role;

}

