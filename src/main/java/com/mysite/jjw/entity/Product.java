package com.mysite.jjw.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드 포함 생성자 자동 생성

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long product_idx; // 상품 번호

    @Column(nullable = false)
    private String productName; // 상품 이름

    @Column(nullable = false)
    private String product_price; // 상품 가격

    @Column(nullable = false)
    private Integer product_pcs; // 상품 수량

    @Column(nullable = false)
    private String productContent; // 상품 정보

    private Integer product_like = 0; // 상품 찜하기 갯수 기본값 0

    private Integer product_view = 0; // 상품 조회 수 기본값 0

    @Column(nullable = false)
    private LocalDate product_at = LocalDate.now();; // 상품 게시일

    private Integer product_sale = 0; // 상품 판매 갯수 기본값 0

    private String product_user; // 상품 판매 회사 및 유저

    private Integer product_coment = 0; // 상품 댓글 갯수 기본값 0

    private String product_image;

}
