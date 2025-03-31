package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    // 상품 리스트 목록 - 검색과 정렬을 동시에 처리하는 메서드
    @Query("SELECT p FROM Product p WHERE " +
            "(p.productName LIKE %:keyword% OR p.productContent LIKE %:keyword%) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'viewst' THEN p.product_view END DESC, " +
            "CASE WHEN :sort = 'salest' THEN p.product_sale END DESC, " +
            "CASE WHEN :sort = 'likest' THEN p.product_like END DESC, " +
            "p.product_idx DESC")
    Page<Product> findAllWithSearchAndSort(@Param("keyword") String keyword, @Param("sort") String sort, Pageable pageable);

    // 상품 리스트 목록 - 기존의 정렬만 처리하는 메서드
    @Query("SELECT p FROM Product p ORDER BY " +
            "CASE WHEN :sort = 'viewst' THEN p.product_view END DESC, " +
            "CASE WHEN :sort = 'salest' THEN p.product_sale END DESC, " +
            "CASE WHEN :sort = 'likest' THEN p.product_like END DESC, " +
            "p.product_idx DESC")
    Page<Product> findAllWithSort(@Param("sort") String sort, Pageable pageable);

    // 로그인한 유저의 좋아요 목록 - 기존의 정렬만 처리하는 메서드
    @Query(value = "SELECT p.* FROM product p " +
            "WHERE p.product_idx IN :productIdxList " +
            "ORDER BY " +
            "CASE WHEN :sort = 'comentst' THEN p.product_coment END DESC, " +
            "CASE WHEN :sort = 'cheapst' THEN CAST(p.product_price AS UNSIGNED) END ASC, " +
            "CASE WHEN :sort = 'expensivest' THEN CAST(p.product_price AS UNSIGNED) END DESC, " +
            "p.product_idx DESC", nativeQuery = true)
    List<Product> findAllWithSort(@Param("sort") String sort, @Param("productIdxList") List<Long> productIdxList);

    // 최근 1주일 동안 가장 많이 팔린 상품 (판매량 순)
    @Query("SELECT p FROM Product p WHERE p.product_at >= :oneWeekAgo ORDER BY p.product_sale DESC")
    List<Product> findTop10BestSelling(@Param("oneWeekAgo") LocalDate oneWeekAgo, Pageable pageable);

    // 최근 1주일 동안 등록된 상품 (등록일 순)
    @Query("SELECT p FROM Product p WHERE p.product_at >= :oneWeekAgo ORDER BY p.product_at DESC")
    List<Product> findTop10NewProducts(@Param("oneWeekAgo") LocalDate oneWeekAgo, Pageable pageable);
}

