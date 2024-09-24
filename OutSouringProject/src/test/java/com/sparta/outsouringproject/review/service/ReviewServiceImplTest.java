package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.review.dto.request.OwnerReviewRequestDto;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.OwnerReviewResponseDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.entity.OwnerReview;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.review.repository.OwnerReviewRepository;
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

    @Mock
    private OwnerReviewRepository ownerReviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceIpml;

    @Test
    void 리뷰등록_성공() throws IOException {
        // given
        Long menuId = 1L;
        String email = "qweasd@asdasd.com";
        User user = new User("1Q2w3e4r@", "피곤하구나", email, Role.USER, false);
        Menu menu = new Menu("name", 12000L);

        MockMultipartFile reviewImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        System.out.println("reviewImage is: " + reviewImage.getOriginalFilename());
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setContents("내용");
        reviewRequestDto.setRating(4);

        // 메뉴와 유저 조회에 대한 mock 설정
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // 이미지 저장에 대한 mock 설정 (이미지 저장 로직이 있는 경우)
//        String imagePath = "images/review/test.jpg";
//        given(reviewServiceIpml.saveReviewImage(any(MultipartFile.class), any())).willReturn(imagePath);
//        System.out.println(imagePath);

        // 리뷰 저장에 대한 mock 설정
        Review savedReview = Review.builder()
                .menu(menu)
                .user(user)
                .contents(reviewRequestDto)
                .rating(reviewRequestDto.getRating())
                .build();

        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        // when
        ReviewResponseDto reviewResponseDto = reviewServiceIpml.createReview(menuId, reviewRequestDto, email, reviewImage);

        // then
        assertNotNull(reviewResponseDto);
        assertEquals(reviewRequestDto.getContents(), reviewResponseDto.getContents());
        assertEquals(reviewRequestDto.getRating(), reviewResponseDto.getRating());
        assertEquals(savedReview.getImage(), reviewResponseDto.getImage());
        assertEquals(user.getUsername(), reviewResponseDto.getUsername());
    }


    @Test
    void 리뷰등록중_회원아이디가_없어_예외처리() throws IOException {
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

        // userRepository에서 null을 받았을 때 예외 발생 설정
        given(menuRepository.findById(isNull())).willThrow(new IllegalArgumentException("회원 아이디가 없습니다."));
//        given(userRepository.findByEmail(isNull())).willThrow(new IllegalArgumentException("회원 아이디가 없습니다."));

        // when & then
        // 예외가 발생하는지 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewServiceIpml.createReview(menu.getMenu_id(), reviewRequestDto, user.getEmail(), image1);
        });
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
    void 리뷰수정_성공() throws IOException {
        // given
        Long reviewId = 1L;
        String email = "qweasd@asdasd.com";
        User user = new User("1Q2w3e4r@", "피곤하구나", email, Role.USER, false);
        ReviewRequestDto exRequestDto = new ReviewRequestDto();
        exRequestDto.setContents("기존 내용");
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setContents("수정된 내용");
        reviewRequestDto.setRating(5);

        Review existingReview = Review.builder()
                .user(user)
                .contents(exRequestDto)
                .rating(4)
                .build();

        // 리뷰와 유저 조회에 대한 mock 설정
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        ReviewResponseDto updatedReviewDto = reviewServiceIpml.updateReview(reviewId, email, reviewRequestDto, null);

        // then
        assertNotNull(updatedReviewDto);
        assertEquals(reviewRequestDto.getContents(), updatedReviewDto.getContents());
        assertEquals(reviewRequestDto.getRating(), updatedReviewDto.getRating());
        assertEquals(user.getUsername(), updatedReviewDto.getUsername());
    }

    @Test
    void deleteReview() {
        //given
        Long reviewId = 1L;
        String email = "qweasd@naver.com";
        User user = new User("1Q2w3e4r@", "피곤하구나", email, Role.USER, false);
        ReflectionTestUtils.setField(user, "id", reviewId);
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setContents("삭제될 것이야");
        Review existingReview = Review.builder()
                .user(user)
                .contents(reviewRequestDto)
                .build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        ReviewResponseDto deleteReviewResponseDto = reviewServiceIpml.deleteReview(reviewId, email);

        //then
        assertNotNull(deleteReviewResponseDto);
    }

    @Test
    void createOwnerComment() {
        //given
        Long reviewId = 1L;
        Long ownerId = 2L;
        Long userId = 1L;
        String ownerEmail = "owner@naver.com";
        String userEmail = "user@naver.com";

        // 실제 리뷰 작성자 (일반 사용자)
        User user = new User("1Q2w3e4r@", "user", userEmail, Role.USER, false);
        ReflectionTestUtils.setField(user, "id", userId);

        // 가게 주인 (OWNER 권한)
        User ownerUser = new User("1Q2w3e4r@", "owner", ownerEmail, Role.OWNER, false);
        ReflectionTestUtils.setField(ownerUser, "id", ownerId);

        Review review = new Review();
        review.setId(reviewId);
        review.setContents("리뷰 내용");
        review.setUser(user);

        OwnerReviewRequestDto ownerReviewRequestDto = new OwnerReviewRequestDto();
        ReflectionTestUtils.setField(ownerReviewRequestDto, "contents", "사장님 댓글");

        // mock 설정: 리뷰와 owner 조회
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(userRepository.findByEmail(ownerUser.getEmail())).willReturn(Optional.of(ownerUser));

        // when
        OwnerReviewResponseDto ownerReviewResponseDto = reviewServiceIpml.createOwnerComment(review.getId(), ownerReviewRequestDto, ownerUser.getEmail());

        // then
        assertNotNull(ownerReviewResponseDto);
        assertEquals(ownerReviewResponseDto.getContents(), "사장님 댓글");
    }
}