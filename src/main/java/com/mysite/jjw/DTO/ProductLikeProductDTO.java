package com.mysite.jjw.DTO;

import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_like;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductLikeProductDTO {
    private Product product;
    private Product_like product_like;
}
