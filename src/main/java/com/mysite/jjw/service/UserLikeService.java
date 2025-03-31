package com.mysite.jjw.service;

import com.mysite.jjw.DTO.ProductLikeProductDTO;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_like;
import com.mysite.jjw.entity.Review_like;
import com.mysite.jjw.repository.ProductLikeRepository;
import com.mysite.jjw.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLikeService {
    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;

    public List<ProductLikeProductDTO> getUserLikes(Long useridx, String sort) {
        // 해당 유저가 찜한 상품들의 ID 가져오기
        List<Long> productIdxList = productLikeRepository.findByProductLikeUseridx(useridx)
                .stream() // 리스트 로 변환하는 메서드
                .map(Product_like::getProductLikeProductidx) // 해당 리뷰 찜하기 상품 번호만 짜름
                .collect(Collectors.toList()); // 짜른 값을 리스트로 모은다

        // 상품 ID 리스트를 기반으로 정렬된 상품 리스트 가져오기
        List<Product> productList = productRepository.findAllWithSort(sort, productIdxList);

        // DTO로 변환하여 반환
        return productList.stream()
                .map(product -> new ProductLikeProductDTO(product, null)) // Product_like 정보가 필요 없다면 null
                .collect(Collectors.toList());
    }





}
