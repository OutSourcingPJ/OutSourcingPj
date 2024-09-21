package com.sparta.outsouringproject.user.controller;

import com.sparta.outsouringproject.user.config.JwtUtil;
import com.sparta.outsouringproject.user.dto.LoginRequestDto;
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
    public String signUp(@RequestParam(name = "password") String password,
                         @RequestParam(name = "username") String username,
                         @RequestParam(name = "email") String email,
                         @RequestParam(name = "role") String role) {
        Role userRole = Role.valueOf(role.toUpperCase()); // 문자열을 Enum으로 변환
        userService.signUp(password, username, email, userRole);
        return "회원가입 성공";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        userService.deleteAccount(email, password);
        return "회원탈퇴 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto,  HttpServletResponse response) {
        String token = userService.login(loginRequestDto, response); // JWT 토큰을 받음
        // 쿠키에 토큰 넣어줘야 함
        jwtUtil.addJwtToCookie(token, response);
        return token;
    }
}