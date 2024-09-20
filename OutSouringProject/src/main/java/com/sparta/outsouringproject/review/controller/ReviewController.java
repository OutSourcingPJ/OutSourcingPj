package com.sparta.outsouringproject.review.controller;

import com.sparta.outsouringproject.review.dto.request.OwnerReviewRequestDto;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.OwnerReviewResponseDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.service.ReviewServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewServiceImpl reviewService;

    // 일반 유저
    @PostMapping("/{menuId}")
    ResponseEntity<ReviewResponseDto> writeReview(@PathVariable Long menuId,
                                                  @Valid @ModelAttribute ReviewRequestDto reviewRequestDto,
                                                  HttpServletRequest request,
                                                  @RequestParam(value = "reviewImage", required = false)
                                                  MultipartFile multipartFile) throws IOException {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.createReview(menuId, reviewRequestDto, email, multipartFile));
    }

    @GetMapping("/{menuId}")
    ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long menuId,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC));
        return ResponseEntity.ok(reviewService.getReview(menuId, pageable));
    }

    @PutMapping("/{reviewId}")
    ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                   @Valid @ModelAttribute ReviewRequestDto reviewRequestDto,
                                                   HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.upateReview(reviewId, email, reviewRequestDto));
    }

    @DeleteMapping("/{reviewId}")
    ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable Long reviewId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, email));
    }

    // 오너
    @PostMapping("/{reviewId}")
    ResponseEntity<OwnerReviewResponseDto> createOwnerComment(@PathVariable Long reviewId,
                                                              @RequestBody OwnerReviewRequestDto reviewRequestDto,
                                                              HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.createOwnerComment(reviewId, reviewRequestDto, email));
    }
}
