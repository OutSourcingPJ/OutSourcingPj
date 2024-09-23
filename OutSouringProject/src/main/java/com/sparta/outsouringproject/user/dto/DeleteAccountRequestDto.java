package com.sparta.outsouringproject.user.dto;

import lombok.Data;

@Data
public class DeleteAccountRequestDto {
    private String email;
    private String password;
}
