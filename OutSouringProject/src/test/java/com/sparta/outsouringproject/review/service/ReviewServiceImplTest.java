package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.review.repository.ReviewRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceIpml;

    @Test
    void 리뷰등록_성공() throws IOException {
        // given
        Menu menu = new Menu("name", 12000L);
        Long userId = 1L;
        User user = new User("1Q2w3e4r@", "피곤하구나", "qweasd@asdasd.com", Role.USER, false);
        ReflectionTestUtils.setField(user, "id", userId);
        MockMultipartFile image1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "test image 2".getBytes());

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setContents("contents");
        reviewRequestDto.setImages(Arrays.asList(image1, image2));
        reviewRequestDto.setRating(4);

        Review review = new Review();
        review.setMenu(menu);
        review.setUser(user);
        review.setContents(reviewRequestDto.getContents());
        review.setRating(reviewRequestDto.getRating());

        given(reviewRepository.save(any(Review.class))).willReturn(review);

        // when
        Review savedReview = reviewRepository.save(review);

        // then
        assertNotNull(savedReview);
        assertEquals(reviewRequestDto.getContents(), savedReview.getContents());
        assertEquals(reviewRequestDto.getRating(), savedReview.getRating());
        assertEquals(user, savedReview.getUser());
        assertEquals(menu, savedReview.getMenu());

    }

    @Test
    void 리뷰조회중_메뉴를_찾지못해_예외처리() {
        //given
        Long menuId = 1L;
        given(menuRepository.findById(anyLong())).willReturn(Optional.empty());

        // when&then
        assertThrows(IllegalArgumentException.class, ()-> reviewServiceIpml.getReview(menuId, Pageable.unpaged()));
    }

    @Test
    void 리뷰조회_성공() {
        //given
        Menu menu = new Menu();
        User user = new User("1Q2w3e4r@", "피곤하구나", "qweasd@asdasd.com", Role.USER, false);
        Review review = new Review();
        review.setUser(user);
        Page<Review> reviewList = new PageImpl<>(List.of(review));
        Pageable pageable = PageRequest.of(0, 10);

        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.findByMenuIdWithOwnerReview(1L, pageable)).willReturn(reviewList);

        // when
        List<ReviewResponseDto> reviewResponseDtoList = reviewServiceIpml.getReview(1L, pageable);

        // then
        assertNotNull(reviewResponseDtoList);
        assertEquals(1, reviewResponseDtoList.size()); // 리뷰 리스트 크기 확인
    }

    @Test
    void updateReview() {
    }

    @Test
    void deleteReview() {
    }

    @Test
    void createOwnerComment() {
    }

    @Test
    void updateOwnerComment() {
    }

    @Test
    void deleteOwnerComment() {
    }
}