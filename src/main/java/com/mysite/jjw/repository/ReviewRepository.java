package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewUserAndReviewProductidxAndReviewProductbuyidx(String reviewUser, Long reviewProductidx , Long reviewProductbuyidx);
    List<Review> findByReviewUser(String reviewUser);
    // 기존의 정렬만 처리하는 메서드
    @Query("SELECT p FROM Review p WHERE p.reviewProductidx = :productidx ORDER BY " +
            "CASE WHEN :sort = 'likest' THEN p.reviewLike END DESC, " +
            "p.reviewIdx DESC")
    Page<Review> findAllWithSortAndReviewProductidx(@Param("sort") String sort, Pageable pageable, @Param("productidx") Long productidx);

}
