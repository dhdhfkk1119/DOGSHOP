package com.mysite.jjw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드 포함 생성자 자동 생성
public class Product_like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productLikeIdx;

    private Long productLikeProductidx;

    private Long productLikeUseridx;

    public Product_like(Long productidx, Long useridx) {
        this.productLikeUseridx = useridx;  // 매개변수 'useridx' 값을 필드에 설정
        this.productLikeProductidx = productidx;  // 매개변수 'productidx' 값을 필드에 설정
    }

}
