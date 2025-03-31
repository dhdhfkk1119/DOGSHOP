package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product_answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productAnswerIdx;

    @Column(nullable = false)
    private String productAnswerContent;

    @Column(nullable = false)
    private Long productAnswerQuestionidx;

    @Column(nullable = false)
    private LocalDate productAnswerAt = LocalDate.now();
}
