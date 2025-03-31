package com.mysite.jjw.controller;

import com.mysite.jjw.entity.Product_answer;
import com.mysite.jjw.service.ProductAnswerService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProductAnswerController {

    private final ProductAnswerService productAnswerService;

    @PostMapping("/product_answer")
    public String addProductAnswer(Model model, @ModelAttribute("product_answer") Product_answer productAnswer,
                                   BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("errors","입력한 값에 오류가 있습니다");
        }        try{
            productAnswerService.registerAnswer(productAnswer);
            model.addAttribute("message", "QnA 답변이 성공적으로 등록되었습니다");
            return "index";
        }catch (Exception e) {
            model.addAttribute("error", "QnA 답변 등록중에 오류가 발생했습니다.");
            return "index";
        }
    }
}
