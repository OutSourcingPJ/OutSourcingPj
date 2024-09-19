package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.review.repository.ReviewRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public ReviewResponseDto createReview(Long menuId, ReviewRequestDto reviewRequestDto, String email) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("작성 권한이 없습니다."));
        Review savedReview = new Review(menu, user, reviewRequestDto);
        reviewRepository.save(savedReview);
        return new ReviewResponseDto(savedReview);
    }

    public List<ReviewResponseDto> getReview(Long menuId) {

        return List.of();
    }
}
