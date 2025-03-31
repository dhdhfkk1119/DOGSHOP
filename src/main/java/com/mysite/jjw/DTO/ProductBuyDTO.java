package com.mysite.jjw.DTO;

import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductBuyDTO {
    Product_buy productBuy;
    Product product;
    private boolean hasReviewed; // 리뷰 작성 여부
}
