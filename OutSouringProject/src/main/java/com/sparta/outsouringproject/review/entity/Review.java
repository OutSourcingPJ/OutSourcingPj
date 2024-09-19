package com.sparta.outsouringproject.review.entity;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public Review(Menu menu, User user, ReviewRequestDto reviewRequestDto) {
        this.id = reviewRequestDto.getId();
        this.contents = reviewRequestDto.getContents();
        this.menu = menu;
        this.user = user;

    }
}
