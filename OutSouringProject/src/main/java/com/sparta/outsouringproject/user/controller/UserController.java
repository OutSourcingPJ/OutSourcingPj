package com.sparta.outsouringproject.user.controller;

import com.sparta.outsouringproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sparta.outsouringproject.user.entity.Role;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String signUp(@RequestParam String id, @RequestParam String password, @RequestParam String username, @RequestParam String role) {
        Role userRole = Role.valueOf(role.toUpperCase()); // 문자열을 Enum으로 변환
        userService.signUp(id, password, username, userRole);
        return "회원가입 성공";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(@RequestParam String id, @RequestParam String password) {
        userService.deleteAccount(id, password);
        return "회원탈퇴 성공";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String id, @RequestParam String password) {
        String token = userService.login(id, password); // JWT 토큰을 받음
        return ResponseEntity.ok(token); // JWT 토큰 반환
    }
}