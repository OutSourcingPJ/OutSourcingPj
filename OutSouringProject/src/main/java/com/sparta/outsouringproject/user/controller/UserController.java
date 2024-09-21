package com.sparta.outsouringproject.user.controller;

import com.sparta.outsouringproject.user.config.JwtUtil;
import com.sparta.outsouringproject.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sparta.outsouringproject.user.entity.Role;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signUp(@RequestParam String password, @RequestParam String username, @RequestParam String email, @RequestParam String role) {
        Role userRole = Role.valueOf(role.toUpperCase()); // 문자열을 Enum으로 변환
        userService.signUp(password, username, email, userRole);
        return "회원가입 성공";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(@RequestParam String email, @RequestParam String password) {
        userService.deleteAccount(email, password);
        return "회원탈퇴 성공";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        String token = userService.login(email, password, response); // JWT 토큰을 받음
        // 쿠키에 토큰 넣어줘야 함
        jwtUtil.addJwtToCookie(token, response);
        return "redirect:/"; // JWT 토큰 반환
    }
}