package com.sparta.outsouringproject.review.repository;

import com.sparta.outsouringproject.review.entity.OwnerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerReviewRepository extends JpaRepository<OwnerReview, Long> {

}
