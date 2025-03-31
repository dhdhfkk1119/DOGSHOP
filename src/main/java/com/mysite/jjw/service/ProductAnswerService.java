package com.mysite.jjw.service;

import com.mysite.jjw.entity.Product_answer;
import com.mysite.jjw.repository.ProductAnswerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductAnswerService {

    private final ProductAnswerRepository productAnswerRepository;

    // 답변 정보 가져오기
    @Transactional
    public Optional<Product_answer> getAnswerInfo(Long questionidx){
        Optional<Product_answer> productAnswer = productAnswerRepository.findByProductAnswerQuestionidx(questionidx);
        return productAnswer;
    }

    // 답변 등록하기
    @Transactional
    public Product_answer registerAnswer(Product_answer product_answer) {

        product_answer.setProductAnswerContent(product_answer.getProductAnswerContent());
        product_answer.setProductAnswerQuestionidx(product_answer.getProductAnswerQuestionidx());
        product_answer.setProductAnswerAt(LocalDate.now());

        return productAnswerRepository.save(product_answer);
    }
}
