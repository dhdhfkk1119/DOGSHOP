package com.mysite.jjw.DTO;

import com.mysite.jjw.entity.Cart;
import com.mysite.jjw.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDTO {
    private Cart cart;        // 장바구니 정보
    private Product product;  // 상품 정보
}
