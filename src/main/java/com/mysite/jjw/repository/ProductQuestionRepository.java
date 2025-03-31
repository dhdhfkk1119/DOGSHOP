package com.mysite.jjw.repository;

import com.mysite.jjw.entity.Product_question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQuestionRepository extends JpaRepository<Product_question, Long> {
    List<Product_question> findByProductQuestionProductidx(Long product_question_productidx);
    List<Product_question> findByProductQuestionUseridx(Long product_question_useridx);
}
