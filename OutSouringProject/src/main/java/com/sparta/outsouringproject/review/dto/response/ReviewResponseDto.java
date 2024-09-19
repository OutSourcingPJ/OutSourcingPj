package com.sparta.outsouringproject.review.dto.response;

import com.sparta.outsouringproject.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String username;
    private String contents;

    public ReviewResponseDto(Review savedReview) {
        this.id = savedReview.getId();
        this.contents = savedReview.getContents();
    }
}
