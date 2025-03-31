package com.mysite.jjw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review_like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewLikeIdx;

    @Column(nullable = false)
    private Long reviewLikeProductidx;

    @Column(nullable = false)
    private Long reviewLikeUseridx;
}
