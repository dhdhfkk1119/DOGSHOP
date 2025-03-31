package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product_question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productQuestionIdx;

    @Column(nullable = false)
    private String productQuestionContent;

    @Column(nullable = false)
    private Long productQuestionUseridx;

    @Column(nullable = false)
    private Long productQuestionProductidx;

    @Column(nullable = false)
    private LocalDateTime productQuestionAt = LocalDateTime.now();
}
