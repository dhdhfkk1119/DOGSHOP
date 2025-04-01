package com.mysite.jjw.controller;

import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_like;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductLikeRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.ProductLikeService;
import com.mysite.jjw.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductLikeController {

    private final ProductLikeService productLikeService;
    private final SignUserRepository signUserRepository;
    private final ProductRepository productRepository;


    // GET 요청: 찜 상태 확인
    @GetMapping("/product/like-status/{productIdx}")
    public ResponseEntity<Map<String, Object>> checkProductLike(@PathVariable Long productIdx, Principal principal) {
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
        int likeCount = productRepository.findById(productIdx)
                .map(Product::getProduct_like) // 좋아요 개수 가져오기
                .orElse(0);
        response.put("likeCount", likeCount);

        // 찜 상태 확인
        boolean isLiked = productLikeService.isProductLiked(productIdx, useridx);
        response.put("status", isLiked ? "liked" : "not_liked");

        return ResponseEntity.ok(response);
    }


    // POST 요청: 찜 추가 및 취소
    @PostMapping("/product/like/{productIdx}")
    public ResponseEntity<Map<String, Object>> toggleProductLike(@PathVariable Long productIdx, Principal principal) {
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
        int likeCount = productRepository.findById(productIdx)
                .map(Product::getProduct_like) // 좋아요 개수 가져오기
                .orElse(0);
        response.put("likeCount", likeCount);

        // 찜 상태 토글
        boolean isLiked = productLikeService.toggleProductLike(productIdx, useridx);
        response.put("status", isLiked ? "added" : "removed");

        return ResponseEntity.ok(response);
    }
}
