package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.review.dto.request.OwnerReviewRequestDto;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.OwnerReviewResponseDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    public ReviewResponseDto createReview(Long menuId, ReviewRequestDto reviewRequestDto, String email, MultipartFile multipartFile)throws IOException;
    public List<ReviewResponseDto> getReview(Long menuId, Pageable pageable);
    public ReviewResponseDto updateReview(Long reviewId, String email, ReviewRequestDto reviewRequestDto, MultipartFile reviewImage)throws IOException;
    public ReviewResponseDto deleteReview(Long reviewId, String email);
    public OwnerReviewResponseDto createOwnerComment(Long reviewId, OwnerReviewRequestDto reviewRequestDto, String email);
    public OwnerReviewResponseDto updateOwnerComment(Long ownerCommentId, OwnerReviewRequestDto ownerReviewRequestDto, String email);
    public OwnerReviewResponseDto deleteOwnerComment(Long ownerCommentId, String email);

    }
