package com.sparta.outsouringproject.review.controller;

import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/menuId")
    ResponseEntity<ReviewResponseDto> writeReview(@PathVariable Long menuId,
                                                  @Valid @ModelAttribute ReviewRequestDto reviewRequestDto,
                                                  HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.createReview(menuId, reviewRequestDto, email));
    }

    @GetMapping("/menu_id")
    ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long menuId,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC));
        return ResponseEntity.ok(reviewService.getReview(menuId, pageable));
    }

    @PutMapping("/reviewId")
    ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                   @Valid @ModelAttribute ReviewRequestDto reviewRequestDto,
                                                   HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.upateReview(reviewId, email, reviewRequestDto));
    }

    @DeleteMapping("/reviewId")
    ResponseEntity<String> deleteReview(@PathVariable Long reviewId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, email));
    }
}
