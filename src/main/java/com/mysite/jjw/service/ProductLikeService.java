package com.mysite.jjw.service;

import com.mysite.jjw.Handler.DataNotFoundException;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_like;
import com.mysite.jjw.repository.ProductLikeRepository;
import com.mysite.jjw.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;

    // 상품이 찜 상태인지 확인
    public boolean isProductLiked(Long productIdx, Long userIdx) {
        Optional<Product_like> existingLike = productLikeRepository.findByProductLikeProductidxAndProductLikeUseridx(productIdx, userIdx);
        return existingLike.isPresent(); // 찜한 상태면 true, 아니면 false
    }
    
    // 상품 찜하기 등록
    @Transactional
    public boolean toggleProductLike(Long productIdx, Long userIdx) {
        // 상품 좋아요 클릭시 기존 Product에 좋아요 수 증가
        Optional<Product> productOpt = productRepository.findById(productIdx);
        if (!productOpt.isPresent()) {
            throw new DataNotFoundException("Product not found"); // 상품이 없으면 예외 처리
        }

        Product product = productOpt.get(); // 실제 Product 객체 가져옴

        // 상품의 좋아요 수 증가
        // 찜 상태 토글 처리
        Optional<Product_like> existingLike = productLikeRepository.findByProductLikeProductidxAndProductLikeUseridx(productIdx, userIdx);

        if (existingLike.isPresent()) {
            // 찜을 취소하는 경우: 좋아요 수를 감소
            productLikeRepository.delete(existingLike.get()); // 찜 삭제
            product.setProduct_like(product.getProduct_like() - 1); // 좋아요 수 감소
            productRepository.save(product); // 좋아요 수 업데이트
            return false; // 삭제된 경우
        } else {
            // 찜을 추가하는 경우: 좋아요 수를 증가
            Product_like productLike = new Product_like();
            productLike.setProductLikeProductidx(productIdx);
            productLike.setProductLikeUseridx(userIdx);
            productLikeRepository.save(productLike); // 찜 추가
            product.setProduct_like(product.getProduct_like() + 1); // 좋아요 수 증가
            productRepository.save(product); // 좋아요 수 업데이트
            return true; // 추가된 경우
        }
    }
}
