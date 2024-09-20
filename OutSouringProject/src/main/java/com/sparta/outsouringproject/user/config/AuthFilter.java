//package com.sparta.outsouringproject.user.config;
//
//import com.sparta.outsouringproject.user.entity.User;
//import com.sparta.outsouringproject.user.repository.UserRepository;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//@Order(1)
//public class AuthFilter implements Filter {
//
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    public AuthFilter(UserRepository userRepository, JwtUtil jwtUtil) {
//        this.userRepository = userRepository;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String url = httpServletRequest.getRequestURI();
//
//        if(StringUtils.hasText(url) && (url.startsWith("/api/user"))) {
//            chain.doFilter(request, response);
//        }else{
//            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);
//
//            if (StringUtils.hasText(tokenValue)) { // 토큰이 존재하면 검증 시작
//                // JWT 토큰 substring
//                String token = jwtUtil.substringToken(tokenValue);
//
//                // 토큰 검증
//                if (!jwtUtil.validateToken(token)) {
//                    throw new IllegalArgumentException("Token Error");
//                }
//
//                // 토큰에서 사용자 정보 가져오기
//                String email = jwtUtil.getEmailFromToken(token);
//
//                User user = userRepository.findByEmail(email).orElseThrow(() ->
//                        new NullPointerException("Not Found User")
//                );
//
//                request.setAttribute("userEmail", email);
//                chain.doFilter(request, response); // 다음 Filter 로 이동
//
//
//            } else {
//                throw new IllegalArgumentException("Not Found Token");
//            }
//        }
//    }
//
//}
