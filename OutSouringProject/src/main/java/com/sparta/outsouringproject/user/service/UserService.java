package com.sparta.outsouringproject.user.service;

import com.sparta.outsouringproject.user.config.JwtUtil;
import com.sparta.outsouringproject.user.config.PasswordEncoder;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sparta.outsouringproject.user.entity.Role;


import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void signUp(String id, String password, String username, Role role) {
        if (userRepository.findById(id).isPresent()) {
            throw new RuntimeException("중복된 사용자 아이디입니다.");
        }

        if (!isValidEmail(id) || !isValidPassword(password)) {
            throw new RuntimeException("이메일 또는 비밀번호 형식이 올바르지 않습니다.");
        }

        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setUsername(username);
        user.setSignupTime(LocalDateTime.now());

        userRepository.save(user);
    }

    public void deleteAccount(String id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.isDeleted()) {
            throw new RuntimeException("이미 탈퇴한 사용자입니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.setDeleted(true); // 탈퇴 처리
        userRepository.save(user);
    }

    public String login(String id, String password) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(passwordRegex, password);
    }
}