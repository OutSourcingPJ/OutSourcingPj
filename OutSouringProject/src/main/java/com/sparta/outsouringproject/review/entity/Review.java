package com.sparta.outsouringproject.review.entity;

import com.sparta.outsouringproject.common.entity.Timestamped;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private List<OwnerReview> ownerReview = new ArrayList<>();


    public Review(Menu menu, User user, ReviewRequestDto reviewRequestDto) {
        this.contents = reviewRequestDto.getContents();
        this.menu = menu;
        this.user = user;

    }

    @Builder
    public Review(Menu menu, User user, ReviewRequestDto contents, String image) {
        this.contents = contents.getContents();
        this.image = image;
        this.user = user;
        this.menu = menu;

    }

    public void update(User user, ReviewRequestDto reviewRequestDto) {
    this.contents = reviewRequestDto.getContents();
    this.user = user;
    }

    public void update(ReviewRequestDto reviewRequestDto, String newReviewImagePath) {
        this.contents = reviewRequestDto.getContents();
        this.image = newReviewImagePath;
    }
}
