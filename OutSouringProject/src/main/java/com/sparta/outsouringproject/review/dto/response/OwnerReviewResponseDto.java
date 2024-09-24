package com.sparta.outsouringproject.review.dto.response;

import com.sparta.outsouringproject.review.entity.OwnerReview;
import lombok.Getter;

import java.util.List;

@Getter
public class OwnerReviewResponseDto {
    private Long reviewId;
    private Long id;
    private String username = "사장님";
    private String contents;

    public OwnerReviewResponseDto(OwnerReview savedReview) {
        this.reviewId = savedReview.getReview().getId();
        this.id = savedReview.getId();
        this.contents = savedReview.getContents();
    }

    public String toString() {
        return "OwnerReviewResponseDto{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", ownerName='" + username + '\'' +
                '}';
    }
}
