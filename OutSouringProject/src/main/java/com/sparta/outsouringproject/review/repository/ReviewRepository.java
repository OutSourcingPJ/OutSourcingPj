package com.sparta.outsouringproject.review.repository;

import com.sparta.outsouringproject.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
