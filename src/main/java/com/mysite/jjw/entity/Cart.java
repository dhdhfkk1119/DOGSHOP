package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드 포함 생성자 자동 생성
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartIdx; // cart_idx -> cartIdx


    @Column(nullable = false)
    private Long cartUseridx; // cart_useridx -> cartUseridx

    @Column(nullable = false)
    private Long cartProductidx; // cart_productidx -> cartProductidx

    @Column(nullable = false)
    private Long cartQuantity; // cart_quantity -> cartQuantity

    @Column(nullable = false, updatable = false)
    private LocalDateTime cartAt = LocalDateTime.now(); // cart_at -> cartAt
}

