package com.mysite.jjw.controller;

import com.mysite.jjw.entity.Product_question;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.ProductQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProductQuestionController {
    private final ProductQuestionService productQuestionService;
    private final SignUserRepository signUserRepository;

    @PostMapping("/product_question")
    public String addProductQuestion(Model model , @ModelAttribute("product_question") Product_question productQuestion,
                                     BindingResult bindingResult,
                                     Principal principal) {

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                        .orElseThrow(() -> new RuntimeException("해당 유저를 찾을수없습니다."));

        
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", "입력한 값에 오류가 있습니다.");
        }
        try{
            productQuestionService.registerProductQuestion(productQuestion,sign.getIdx());
            model.addAttribute("message", "QnA가 정공적으로 등록 되었습니다.");
            return "index";
        }catch (Exception e) {
            model.addAttribute("error", "QnA 등록중에 오류가 발생했습니다.");
            return "index";
        }
    }
}
