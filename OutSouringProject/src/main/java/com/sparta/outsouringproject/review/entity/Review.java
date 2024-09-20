package com.sparta.outsouringproject.review.entity;

import com.sparta.outsouringproject.common.entity.Timestamped;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor
@Data
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
        this.id = reviewRequestDto.getId();
        this.contents = reviewRequestDto.getContents();
        this.menu = menu;
        this.user = user;

    }

    @Builder
    public Review(Menu menu, User user, String contents, String image) {
        this.contents = contents;
        this.image = image;
        this.user = user;
        this.menu = menu;

    }

    public void update(Review review, User user, ReviewRequestDto reviewRequestDto) {
    this.id = review.getId();
    this.contents = reviewRequestDto.getContents();
    this.user = user;
    }
}
