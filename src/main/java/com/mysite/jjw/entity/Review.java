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
@Table(name = "review")
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드 포함 생성자 자동 생성
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;

    @Column(nullable = false)
    private String reviewContent;

    private LocalDateTime reviewAt = LocalDateTime.now();

    @Column(nullable = false)
    private String reviewUser;

    @Column(nullable = false)
    private Long reviewProductidx;

    private Long reviewLike = 0L; // 댓글 찜하기

    private String reviewImage;
    
    @Column(nullable = false)
    private int reviewScope; // 별점

    @Column(nullable = false)
    private Long reviewProductbuyidx;
}
