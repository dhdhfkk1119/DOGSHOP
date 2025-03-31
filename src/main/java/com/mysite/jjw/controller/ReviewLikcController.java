package com.mysite.jjw.controller;

import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ReviewRepository;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReviewLikcController {

    private final SignUserRepository signUserRepository;
    private final ReviewLikeService reviewLikeService;
    private final ReviewRepository reviewRepository;

    // GET 요청: 찜 상태 확인
    @GetMapping("/review/like-status/{reviewproductIdx}")
    public ResponseEntity<Map<String, Object>> checkReviewLike(@PathVariable Long reviewproductIdx, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        // 로그인 확인
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }

        // 로그인한 유저 정보 가져오기
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
        Long useridx = sign.getIdx();

        // 찜 개수 조회
        Long likeCount = reviewRepository.findById(reviewproductIdx)
                .map(Review::getReviewLike) // 좋아요 개수 가져오기
                .orElse(0L);
        response.put("likeCount", likeCount);

        // 찜 상태 확인
        boolean isLiked = reviewLikeService.isReviewLiked(reviewproductIdx, useridx);
        response.put("status", isLiked ? "liked" : "not_liked");

        return ResponseEntity.ok(response);
    }

    // POST 요청: 찜 추가 및 취소
    @PostMapping("/review/like/{reviewproductIdx}")
    public ResponseEntity<Map<String, Object>> toggleReviewLike(@PathVariable Long reviewproductIdx, Principal principal) {

        Map<String, Object> response = new HashMap<>();

        // 로그인 확인
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }

        // 찜 개수 조회
        Long likeCount = reviewRepository.findById(reviewproductIdx)
                .map(Review::getReviewLike) // 좋아요 개수 가져오기
                .orElse(0L);
        response.put("likeCount", likeCount);

        // 로그인한 유저 정보 가져오기
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
        Long useridx = sign.getIdx();

        // 찜 상태 토글
        boolean isLiked = reviewLikeService.toggleReviewLike(reviewproductIdx, useridx);
        response.put("status", isLiked ? "added" : "removed");

        return ResponseEntity.ok(response);
    }
}
