package com.mysite.jjw.service;

import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductCancelService {

    private final ProductBuyRepository productBuyRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    //상품 캔슬 정보만 가져오기
    @Transactional
    public List<ProductBuyDTO> getCancel(Long useridx, String search) {
        // ✅ 특정 유저의 구매 내역을 가져옴
        List<Product_buy> buyItems = productBuyRepository.findByProductBuyUseridx(useridx);

        // ✅ Java 스트림을 이용하여 필터링 (completed, delivery 상태만)
        List<ProductBuyDTO> productBuyDTOs = new ArrayList<>();
        for (Product_buy productBuy : buyItems) {
            if (!"cancel".equals(productBuy.getProductStatus()) ) {
                continue; // ✅ 배송 완료 및 구매 완료 상태가 아니면 스킵
            }

            Optional<Product> product = productRepository.findById(productBuy.getProductBuyProductidx());
            if (product.isPresent()) {
                Product p = product.get();

                // ✅ 검색어가 없거나, 상품명에 검색어가 포함된 경우만 추가
                if (search == null || p.getProductName().toLowerCase().contains(search.toLowerCase())) {
                    boolean hasReviewed = reviewRepository.existsByReviewUserAndReviewProductidxAndReviewProductbuyidx(useridx.toString(), p.getProduct_idx() , productBuy.getProductBuyIdx());
                    productBuyDTOs.add(new ProductBuyDTO(productBuy, p , hasReviewed));
                }
            }
        }
        return productBuyDTOs;
    }

    //상품 구매취소로 변경
    @Transactional
    public boolean cancelOrder(Long productBuyProductIdx) {
        Optional<Product_buy> productBuy = productBuyRepository.findById(productBuyProductIdx);


        if (!productBuy.isPresent()) {
            return false; // 상품을 찾을 수 없으면 false 반환
        }

        Product_buy productBuyEntity = productBuy.get();
        Optional<Product> productOpt = productRepository.findById(productBuyEntity.getProductBuyProductidx()); // 구매한 상품의 상품 번호로 조회
        Product product = productOpt.get();


        // 구매한 수량 가져오기 (Long → int 변환)
        int buyQuantity = productBuyEntity.getProduct_buy_quantity().intValue();

        // product_sale 차감
        int updatedSale = product.getProduct_sale() - buyQuantity;
        product.setProduct_sale(Math.max(updatedSale, 0)); // 음수 방지

        product.setProduct_pcs(product.getProduct_pcs() + buyQuantity);
        // 상태를 cancel로 변경
        productBuyEntity.setProductStatus("cancel");


        // 변경된 상품 정보를 저장
        productBuyRepository.save(productBuyEntity);
        productRepository.save(product);

        return true; // 취소 처리 성공
    }
}
