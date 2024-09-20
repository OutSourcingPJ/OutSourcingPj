package com.sparta.outsouringproject.review.entity;

import com.sparta.outsouringproject.review.dto.request.OwnerReviewRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ownerReview")
@Getter
@NoArgsConstructor

public class OwnerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reviewId;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public OwnerReview(Review review, User user, OwnerReviewRequestDto reviewRequestDto) {
        this.reviewId = review.getId();
        this.user = user;
        this.contents = reviewRequestDto.getContents();
    }
}
