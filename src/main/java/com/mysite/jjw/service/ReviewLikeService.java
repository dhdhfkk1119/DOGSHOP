package com.mysite.jjw.service;

import com.mysite.jjw.Handler.DataNotFoundException;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_like;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Review_like;
import com.mysite.jjw.repository.ReviewLikeRepository;
import com.mysite.jjw.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;

    // 댓글이 찜 상태인지 확인
    public boolean isReviewLiked(Long reviewproductIdx, Long userIdx) {
        Optional<Review_like> existingLike = reviewLikeRepository.findByReviewLikeUseridxAndReviewLikeProductidx(userIdx,reviewproductIdx);
        return existingLike.isPresent(); // 찜한 상태면 true, 아니면 false
    }

    @Transactional
    public boolean toggleReviewLike(Long reviewproductIdx, Long userIdx) {
        // 상품 좋아요 클릭시 기존 Review에 좋아요 수 증가
        Optional<Review> reviewOpt = reviewRepository.findById(reviewproductIdx);
        if (!reviewOpt.isPresent()) {
            throw new DataNotFoundException("Product not found"); // 상품이 없으면 예외 처리
        }

        Review review = reviewOpt.get(); // 실제 review 객체 가져옴

        // 상품의 좋아요 수 증가
        // 찜 상태 토글 처리
        Optional<Review_like> existingLike = reviewLikeRepository.findByReviewLikeUseridxAndReviewLikeProductidx(userIdx,reviewproductIdx);

        if (existingLike.isPresent()) {
            // 찜을 취소하는 경우: 좋아요 수를 감소
            reviewLikeRepository.delete(existingLike.get()); // 찜 삭제
            review.setReviewLike(review.getReviewLike() - 1); // 좋아요 수 감소
            reviewRepository.save(review); // 좋아요 수 업데이트
            return false; // 삭제된 경우
        } else {
            // 찜을 추가하는 경우: 좋아요 수를 증가
            Review_like review_like = new Review_like();
            review_like.setReviewLikeProductidx(reviewproductIdx);
            review_like.setReviewLikeUseridx(userIdx);
            reviewLikeRepository.save(review_like); // 찜 추가
            review.setReviewLike(review.getReviewLike() + 1); // 좋아요 수 증가
            reviewRepository.save(review); // 좋아요 수 업데이트
            return true; // 추가된 경우
        }
    }

}
