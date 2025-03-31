package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Review_like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<Review_like,Long> {
    Optional<Review_like> findByReviewLikeUseridxAndReviewLikeProductidx(Long reviewLikeUser, Long reviewLikeProductidx);
}
