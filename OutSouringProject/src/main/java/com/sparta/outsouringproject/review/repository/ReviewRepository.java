package com.sparta.outsouringproject.review.repository;

import com.sparta.outsouringproject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByMenuIdOrderByCreateAtDesc(Pageable pageable, Long menuId);
}
