package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드 포함 생성자 자동 생성
public class Product_buy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productBuyIdx; // 상품 판매 번호

    @Column(nullable = false)
    private Long productBuyProductidx; // 상품 번호

    @Column(nullable = false)
    private Long productBuyUseridx; // 상품 유저 번호

    @Column(nullable = false)
    private Long product_buy_quantity; // 상품 구매 개수

    @Column(nullable = false, updatable = false)
    private LocalDateTime product_buy_at = LocalDateTime.now(); // 상품 이름

    @Column(nullable = false)
    private String productStatus;

}
