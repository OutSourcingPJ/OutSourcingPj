package com.sparta.outsouringproject.review.service;

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
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.exec.ExecutionException;
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
    private final OwnerReviewRepository ownerReviewRepository;

    @Value("${file.upload_dir}")
    private String uploadDir;

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long menuId, ReviewRequestDto reviewRequestDto,
                                          String email, MultipartFile reviewImage) throws IOException {

        //게시물 생성
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("작성 권한이 없습니다."));

        String imagePath = null;
        if (reviewImage != null && !reviewImage.isEmpty()) {
            imagePath = saveReviewImage(reviewImage, email);
        }

        //게시물, 이미지 한번에 처리
        Review savedReview = Review.builder()
                .menu(menu)
                .user(user)
                .contents(reviewRequestDto)
                .image(imagePath)
                .rating(reviewRequestDto.getRating())
                .build();

        savedReview = reviewRepository.save(savedReview);

        return ReviewResponseDto.from(savedReview);
    }

    @Override
    public List<ReviewResponseDto> getReview(Long menuId, Pageable pageable) {
        menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        Page<Review> review = reviewRepository.findByMenuIdWithOwnerReview(menuId, pageable);
        return review.getContent().stream().map(ReviewResponseDto::new).toList();
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, String email, ReviewRequestDto reviewRequestDto, MultipartFile reviewImage) throws IOException {
        Review updateReview = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("수정 권한이 없습니다."));

//        String newReviewImagePath = null;
//        if (reviewImage != null && !reviewImage.isEmpty()) {
//            deleteExistingReviewImage(updateReview);
//            newReviewImagePath = saveReviewImage(reviewImage, user.getEmail());
//        }

        updateReview.update(user, reviewRequestDto);
//        reviewRepository.save(updateReview);

        return new ReviewResponseDto(updateReview);
    }


    @Override
    public ReviewResponseDto deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("삭제 권한이 없습니다."));
        reviewRepository.delete(review);
        return new ReviewResponseDto(review);
    }

    @Override
    @Transactional
    public OwnerReviewResponseDto createOwnerComment(Long reviewId, OwnerReviewRequestDto reviewRequestDto, String email) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("작성 권한이 없습니다.")); // 이넘 활용하여 수정할것
        if(user.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("작성 권한이 없습니다.");
        }
        OwnerReview savedReview = new OwnerReview(review, user, reviewRequestDto);
        ownerReviewRepository.save(savedReview);
        return new OwnerReviewResponseDto(savedReview);
    }

    @Override
    @Transactional
    public OwnerReviewResponseDto updateOwnerComment(Long ownerCommentId, OwnerReviewRequestDto ownerReviewRequestDto, String email) {
        OwnerReview ownerReview = ownerReviewRepository.findById(ownerCommentId).orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("게시글을 수정 할 권한이 없습니다."));
        ownerReview.update(user, ownerReviewRequestDto);
        return new OwnerReviewResponseDto(ownerReview);
    }

    @Override
    public OwnerReviewResponseDto deleteOwnerComment(Long ownerCommentId, String email) {
        OwnerReview ownerReview = ownerReviewRepository.findById(ownerCommentId).orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("게시글을 삭제 할 권한이 없습니다."));
        ownerReviewRepository.delete(ownerReview);
        return new OwnerReviewResponseDto(ownerReview);
    }

    public String saveReviewImage(MultipartFile reviewImage, String email) throws IOException {
        String userDir = uploadDir + "/" + email;
        Path userPath = Paths.get(userDir);

        if(!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(reviewImage.getOriginalFilename()));
        Path targetLocation = userPath.resolve(fileName);

        Files.copy(reviewImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }



//    private void deleteExistingReviewImage(Review review) {
//        String existingReviewImage = review.getImage();
//        if (existingReviewImage != null && !existingReviewImage.isEmpty()) {
//            try {
//                Path fileToDeletePath = Paths.get(existingReviewImage);
//                Files.deleteIfExists(fileToDeletePath);
//            } catch (IOException e) {
//                System.err.println("리뷰 이미지 삭제 중 오류 발생: " + e.getMessage());
//            }
//        }
//    }
}
