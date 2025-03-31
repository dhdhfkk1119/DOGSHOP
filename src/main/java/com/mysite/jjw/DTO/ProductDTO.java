package com.mysite.jjw.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @NotBlank(message = "상품명을 입력해주세요.")
    private String productName; // 상품 이름

    @NotBlank(message = "상품 가격을 입력해주세요.")
    private String product_price; // 상품 가격

    @NotNull(message = "상품 수량을 입력해주세요.")
    private Integer product_pcs; // 상품 수량

    @NotBlank(message = "상품 정보를 입력해주세요.")
    private String productContent; // 상품 정보

    private String product_image;

    private Integer product_like = 0; // 상품 좋아요 기본값 0
    private Integer product_view = 0; // 상품 조회 기본값 0
    private Integer product_sale = 0; // 상품 판매 기본값 0
    private Integer product_coment = 0; // 상품 댓글 기본값 0
    private String product_user; // 상품 등록 유저 정보
}
