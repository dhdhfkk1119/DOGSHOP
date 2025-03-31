package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "member")
@Getter
@Setter

public class Sign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 자동 증가 NUM

    @Column(nullable = false , unique = true)
    private String id; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false,unique = true)
    private String name; // 이름 별명

    @Column(nullable = false)
    private String sex; // 성별

    @Column(nullable = false,unique = true)
    private String phone; //전화 번호

    @Column(nullable = false)
    @CreatedDate
    private LocalDate memberDate = LocalDate.now();

    @Column(nullable = false,unique = true)
    private String email; //이메일

    @Column(nullable = false)
    private LocalDate age; // 나이

    private String memberImage = "basic.png"; // 이미지

}
