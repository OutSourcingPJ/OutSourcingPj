package com.sparta.outsouringproject.review.dto.response;

import com.sparta.outsouringproject.review.entity.OwnerReview;
import lombok.Getter;

@Getter
public class OwnerReviewResponseDto {
    private Long reviewId;
    private Long id;
    private String username = "사장님";
    private String contents;

    public OwnerReviewResponseDto(OwnerReview savedReview) {
        this.id = savedReview.getId();
        this.reviewId = savedReview.getReviewId();
        this. contents = savedReview.getContents();
    }
}
