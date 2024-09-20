package com.sparta.outsouringproject.review.dto.response;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.user.entity.User;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String username;
    private String contents;
    private String image;

    public ReviewResponseDto(Review savedReview) {
        this.id = savedReview.getId();
        this.contents = savedReview.getContents();
    }

    public ReviewResponseDto(User user, Menu menu, String contents, String image) {
        this.username = user.toString();
        this.contents = contents;
        this.image = image;
    }

    public static ReviewResponseDto from(Review review) {

        return new ReviewResponseDto(
                review.getUser(),
                review.getMenu(),
                review.getContents(),
                review.getImage()
        );
    }
}
