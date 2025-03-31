package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignUserRepository extends JpaRepository<Sign,Long> {
    Optional<Sign> findById(String id); //JPA가 username 필드로 검색하는 커스텀 쿼리를 생성

    boolean existsById(String id);
    boolean existsByEmail(String email);

}
