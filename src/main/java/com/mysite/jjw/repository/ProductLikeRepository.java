package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Product_like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<Product_like,Long> {
    Optional<Product_like> findByProductLikeProductidxAndProductLikeUseridx(Long productLikeProductidx, Long productLikeUseridx);
    List<Product_like> findByProductLikeUseridx(Long productLikeUseridx);
}
