package com.sparta.outsouringproject.user.service;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.user.config.JwtUtil;
import com.sparta.outsouringproject.user.config.PasswordEncoder;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public void signUp(String password, String username, String email, Role role) {
        if (userRepository.findByEmail(email).isPresent()) { // 이메일로 중복 체크
            throw new RuntimeException("중복된 사용자 아이디입니다.");
        }

        if (!isValidEmail(email) || !isValidPassword(password)) {
            throw new RuntimeException("이메일 또는 비밀번호 형식이 올바르지 않습니다.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEmail(email);
        user.setUsername(username);
        user.setSignupTime(LocalDateTime.now());


        user = userRepository.save(user);

        // 카트도 함께 생성
        Cart cart = new Cart();
        cart.relatedUser(user);
        cartRepository.save(cart);
    }

    public void deleteAccount(String email, String password) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.isDeleted()) {
            throw new RuntimeException("이미 탈퇴한 사용자입니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.setDeleted(true); // 탈퇴 처리

        // 장바구니 같이 삭제
        cartRepository.deleteByUser(user);

        userRepository.save(user);
    }

    public String login(String email, String password, HttpServletResponse response) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        String token = jwtUtil.createToken(user.getEmail());
        jwtUtil.addJwtToCookie(token, response);
        return token;
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