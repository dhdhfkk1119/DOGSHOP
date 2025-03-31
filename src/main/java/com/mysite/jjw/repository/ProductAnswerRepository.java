package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Product_answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductAnswerRepository extends JpaRepository<Product_answer , Long> {
    Optional<Product_answer> findByProductAnswerQuestionidx(Long questionidx);
}
