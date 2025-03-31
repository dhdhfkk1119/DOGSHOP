package com.mysite.jjw.service;


import com.mysite.jjw.DTO.CartProductDTO;
import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Cart;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBuyService {
    private final ProductBuyRepository productBuyRepository;
    private final ProductRepository productRepository;
    private final SignUserRepository signUserRepository;


    // 구매하기 API
    @Transactional
    public Product_buy registerBuy(Long product_buy_productidx , Long quantity ,Long product_buy_useridx ){

        // 1. 상품 정보 가져오기
        Product product = productRepository.findById(product_buy_productidx)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 2. 구매자 정보 가져오기
        Sign sign = signUserRepository.findById(product_buy_useridx)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 재고 확인 (재고보다 많이 구매할 수 없음)
        if (product.getProduct_pcs() < quantity) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        Product_buy productBuy = new Product_buy();

        productBuy.setProductBuyProductidx(product_buy_productidx); // 구매한 상품 IDX
        productBuy.setProduct_buy_quantity(quantity); // 구매 수량
        productBuy.setProductBuyUseridx(product_buy_useridx); // 구매 한 유저 IDX값
        productBuy.setProduct_buy_at(LocalDateTime.now()); // 구매 시간
        productBuy.setProductStatus("delivery");
        product.setProduct_pcs((int) (product.getProduct_pcs() - quantity));
        productRepository.save(product);

        return productBuyRepository.save(productBuy);
    }
}
