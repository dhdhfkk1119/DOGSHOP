package com.mysite.jjw.service;

import com.mysite.jjw.entity.Product_question;
import com.mysite.jjw.repository.ProductQuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQuestionService {
    private final ProductQuestionRepository productQuestionRepository;

    // QnA질문 목록 가져오기
    @Transactional
    public List<Product_question> getQuestionInfo(Long productidx) {
        List<Product_question> productQuestions = productQuestionRepository.findByProductQuestionProductidx(productidx);
        return productQuestions;
    }

    // QnA 등록하기
    @Transactional
    public Product_question registerProductQuestion(Product_question productQuestion,Long useridx) {

        productQuestion.setProductQuestionContent(productQuestion.getProductQuestionContent());
        productQuestion.setProductQuestionUseridx(useridx);
        productQuestion.setProductQuestionProductidx(productQuestion.getProductQuestionProductidx());
        productQuestion.setProductQuestionAt(LocalDateTime.now());

        return productQuestionRepository.save(productQuestion);
    }
}
