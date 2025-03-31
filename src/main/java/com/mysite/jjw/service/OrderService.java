package com.mysite.jjw.service;

import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.ReviewRepository;
import com.mysite.jjw.repository.SignUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductBuyRepository productBuyRepository;
    private final ProductRepository productRepository;
    private final SignUserRepository signUserRepository;
    private final ReviewRepository reviewRepository;

    // 구매 정보 (배송 완료 및 구매완료만)
    @Transactional
    public List<ProductBuyDTO> getBuyItems(Long useridx, String search) {
        List<Product_buy> buyItems = productBuyRepository.findByProductBuyUseridx(useridx);

        List<ProductBuyDTO> productBuyDTOs = new ArrayList<>();
        for (Product_buy productBuy : buyItems) {
            if (!"delivery".equals(productBuy.getProductStatus()) && !"completed".equals(productBuy.getProductStatus())) {
                continue; // ✅ 배송 완료 및 구매 완료 상태가 아니면 스킵
            }

            Optional<Product> product = productRepository.findById(productBuy.getProductBuyProductidx());
            if (product.isPresent()) {
                Product p = product.get();

                // ✅ 검색어가 없거나, 상품명에 검색어가 포함된 경우만 추가
                if (search == null || p.getProductName().toLowerCase().contains(search.toLowerCase())) {
                    boolean hasReviewed = reviewRepository.existsByReviewUserAndReviewProductidxAndReviewProductbuyidx(useridx.toString(), p.getProduct_idx(),productBuy.getProductBuyIdx());

                    productBuyDTOs.add(new ProductBuyDTO(productBuy, p , hasReviewed));
                }
            }
        }
        return productBuyDTOs;
    }

    // 구매 상품 번호랑 구매한 유저 번호를 리뷰 번호랑 리뷰작성자로 비교
    @Transactional
    public boolean hasUserReviewedProduct(Long Useridx, Long productIdx,Long productbuyidx) {
        Sign sign = signUserRepository.findById(Useridx)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));
        // 리뷰 작성 여부 확인
        return reviewRepository.existsByReviewUserAndReviewProductidxAndReviewProductbuyidx(sign.getId(), productIdx , productbuyidx);
    }

    // 지금까지 구매한 총 가격 (배송 완료 및 구매완료만)
    public Map<String, Integer> getTotalPriceAndQuantity(Long userIdx) {
        List<Product_buy> buyItems = productBuyRepository.findByProductBuyUseridx(userIdx);

        // ✅ Java 스트림을 이용하여 필터링 (completed, delivery 상태만)
        List<Product_buy> filteredItems = buyItems.stream()
                .filter(item -> "delivery".equals(item.getProductStatus()) || "completed".equals(item.getProductStatus()))
                .collect(Collectors.toList());

        int totalPrice = 0;
        int totalQuantity = 0;

        for (Product_buy item : filteredItems) {
            Optional<Product> product = productRepository.findById(item.getProductBuyProductidx());
            if (product.isPresent()) {
                int price = Integer.parseInt(product.get().getProduct_price()); // 상품 가격
                int quantity = Math.toIntExact(item.getProduct_buy_quantity()); // 구매 수량
                totalPrice += price * quantity;
                totalQuantity += quantity;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("totalPrice", totalPrice);
        result.put("totalQuantity", totalQuantity);
        return result;
    }

    //상품 구매확정으로 변경
    @Transactional
    public boolean productCompleted(Long productBuyProductIdx) {
        Optional<Product_buy> productBuy = productBuyRepository.findById(productBuyProductIdx);

        if (!productBuy.isPresent()) {
            return false; // 상품을 찾을 수 없으면 false 반환
        }

        Product_buy productBuyEntity = productBuy.get();

        // 상태를 completed 변경
        productBuyEntity.setProductStatus("completed");

        // 변경된 상품 정보를 저장
        productBuyRepository.save(productBuyEntity);

        return true; // 취소 처리 성공
    }


}
