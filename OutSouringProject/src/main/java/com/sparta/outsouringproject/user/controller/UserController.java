package com.sparta.outsouringproject.user.controller;

import com.sparta.outsouringproject.user.config.JwtUtil;
import com.sparta.outsouringproject.user.dto.DeleteAccountRequestDto;
import com.sparta.outsouringproject.user.dto.LoginRequestDto;
import com.sparta.outsouringproject.user.dto.UserRequestDto;
import com.sparta.outsouringproject.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.sparta.outsouringproject.user.entity.Role;

@RestController
// @Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signUp(@RequestBody UserRequestDto userRequestDto) {
        Role userRole = Role.valueOf(userRequestDto.getRole().toUpperCase());
        userService.signUp(userRequestDto.getPassword(), userRequestDto.getUsername(), userRequestDto.getId(), userRole);
        return "회원가입 성공";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(@RequestBody DeleteAccountRequestDto deleteAccountRequestDto) {
        userService.deleteAccount(deleteAccountRequestDto.getEmail(), deleteAccountRequestDto.getPassword());
        return "회원탈퇴 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String token = userService.login(loginRequestDto, response);
        jwtUtil.addJwtToCookie(token, response);
        return token;
    }
}