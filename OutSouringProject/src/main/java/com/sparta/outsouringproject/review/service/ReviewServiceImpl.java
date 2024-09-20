package com.sparta.outsouringproject.review.service;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.review.dto.request.ReviewRequestDto;
import com.sparta.outsouringproject.review.dto.response.ReviewResponseDto;
import com.sparta.outsouringproject.review.entity.Review;
import com.sparta.outsouringproject.review.repository.ReviewRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Value("$(file.upload_dir)")
    private String uploadDir;

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long menuId, ReviewRequestDto reviewRequestDto, String email, MultipartFile multipartFile) throws IOException {

        //게시물 생성
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("작성 권한이 없습니다."));
        String imagePath = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            imagePath = saveImage(multipartFile, email);
        }

        Review savedReview = Review.builder()
                .menu(menu)
                .user(user)
                .contents(reviewRequestDto.getContents())
                .image(imagePath)
                .build();

        savedReview = reviewRepository.save(savedReview);

        return ReviewResponseDto.from(savedReview);
    }

    @Override
    public List<ReviewResponseDto> getReview(Long menuId, Pageable pageable) {
        menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        Page<Review> review = reviewRepository.findByMenuIdOrderByIdDesc(menuId, pageable);
        return review.getContent().stream().map(ReviewResponseDto::new).toList();
    }

    @Override
    @Transactional
    public ReviewResponseDto upateReview(Long reviewId, String email, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("수정 권한이 없습니다."));
        review.update(review, user, reviewRequestDto);
        return new ReviewResponseDto(review);
    }

    @Override
    public ReviewResponseDto deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("삭제 권한이 없습니다."));
        reviewRepository.delete(review);
        return new ReviewResponseDto(review);
    }

    private String saveImage(MultipartFile multipartFile, String email) throws IOException {
        String userDir = uploadDir + "/" + email;
        Path userPath = Paths.get(userDir);

        if(!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        Path targetLocation = userPath.resolve(fileName);

        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }
}
