package com.sparta.outsouringproject.review.entity;

import com.sparta.outsouringproject.review.dto.request.OwnerReviewRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ownerReview")
@Getter
@NoArgsConstructor
@Setter

public class OwnerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public OwnerReview(Review review, User user, OwnerReviewRequestDto reviewRequestDto) {
        this.user = user;
        this.contents = reviewRequestDto.getContents();
        this.review = review;
    }

    public OwnerReview(OwnerReview ownerReview, User user, OwnerReviewRequestDto ownerReviewRequestDto) {
        this.id = ownerReview.getId();
        this.user = user;
        this.contents = ownerReviewRequestDto.getContents();
    }

    public OwnerReview(OwnerReview ownerReview, User user) {
        this.id = ownerReview.getId();
        this.user = user;
    }

    public void update(User user, OwnerReviewRequestDto ownerReviewRequestDto) {
        this.user = user;
        this.contents = ownerReviewRequestDto.getContents();
    }
}
