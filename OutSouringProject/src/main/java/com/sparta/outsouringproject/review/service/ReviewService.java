package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.review.repository.ReviewRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDto createReview(Long menuId, ReviewRequestDto reviewRequestDto, String email) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("작성 권한이 없습니다."));
        Review savedReview = new Review(menu, user, reviewRequestDto);
        reviewRepository.save(savedReview);
        return new ReviewResponseDto(savedReview);
    }

    public List<ReviewResponseDto> getReview(Long menuId, Pageable pageable) {
        menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        Page<Review> review = reviewRepository.findByMenuIdOrderByCreateAtDesc(pageable, menuId);
        return review.getContent().stream().map(ReviewResponseDto::new).toList();
    }

    @Transactional
    public ReviewResponseDto upateReview(Long reviewId, String email, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("수정 권한이 없습니다."));
        review.update(review, user, reviewRequestDto);
        return new ReviewResponseDto(review);
    }

    public String deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("삭제 권한이 없습니다."));
        reviewRepository.delete(review);
        return review.getId() + "의 리뷰가 삭제되었습니다.";
    }
}
