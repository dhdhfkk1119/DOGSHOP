package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Product_buy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBuyRepository extends JpaRepository<Product_buy , Long> {

    List<Product_buy> findByProductBuyUseridx(Long productBuyUseridx);

    Optional<Product_buy> findByProductBuyIdxAndProductBuyUseridx(Long productBuyidx, Long useridx);
}
