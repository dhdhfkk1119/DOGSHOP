package com.mysite.jjw.service;

import com.mysite.jjw.entity.*;
import com.mysite.jjw.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnapService {

    public final ReviewRepository reviewRepository;
    public final SignUserRepository signUserRepository;
    public final ProductQuestionRepository productQuestionRepository;
    public final ProductRepository productRepository;
    public final ProductAnswerRepository productAnswerRepository;
    
    //유저가 질문에 대한 답변을 가져옴
    public Optional<Product_answer> getAnswerList(Long questionidx){
        Optional<Product_answer> answer = productAnswerRepository.findByProductAnswerQuestionidx(questionidx);
        return answer;
    }


    // 해당 유저가 보낸 질문 정보에 상품 정보를 가져옴
    public Optional<Product> getQuestionProduct(Long productidx){
         Optional<Product> products = productRepository.findById(productidx);
         return products;
    }


    //snap에서 Qna정보 가져오기
    public List<Product_question> getquestionList(Long useridx){
        List<Product_question> questionList = productQuestionRepository.findByProductQuestionUseridx(useridx);
        return questionList;
    }


    // snap 에서 리뷰 정보 가져오기
    public List<Review> getReviewInfo(String username){
        List<Review> reviewList = reviewRepository.findByReviewUser(username);

        return reviewList;
    }

    // snap 이름 변경하기
    @Transactional
    public boolean snapRename(Long userIdx , String rename){
        // 해당 유저 번호를 찾기
        Sign sign = signUserRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. "));

        // 이름 변경
        sign.setName(rename);

        // DB에 저장
        signUserRepository.save(sign);

        // 업데이트 성공 여부 리턴
        return true;
    }
}
