package com.sparta.outsouringproject.review.repository;

import com.sparta.outsouringproject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.ownerReview WHERE r.menu.menu_id = :menuId")
    Page<Review> findByMenuIdWithOwnerReview(@Param("menuId") Long menuId, Pageable pageable);


}
