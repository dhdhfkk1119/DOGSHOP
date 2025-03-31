package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCartUseridxAndCartProductidx(Long cartUseridx, Long cartProductidx); // 장바구니 중복확인

    List<Cart> findByCartUseridx(Long cartUseridx); // 사용자별 장바구니 조회

}

