package com.sparta.outsouringproject.review.dto.response;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.user.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String username;
    private String contents;
    private String image;
    private List<OwnerReviewResponseDto> ownerReviews;

    public ReviewResponseDto(Review savedReview) {
        this.id = savedReview.getId();
        this.contents = savedReview.getContents();
        this.username = savedReview.getUser().getUsername();
        this.image = savedReview.getImage();
        this.ownerReviews = savedReview.getOwnerReview().stream()
                .map(OwnerReviewResponseDto::new)
                .collect(Collectors.toList());
    }


    public ReviewResponseDto(Long review, User user, String reviewRequestDto, String image) {
        this.id = review;
        this.username = user.getUsername();
        this.contents = reviewRequestDto;
        this.image = image;
    }


    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUser(),
                review.getContents(),
                review.getImage()
        );
    }
}
