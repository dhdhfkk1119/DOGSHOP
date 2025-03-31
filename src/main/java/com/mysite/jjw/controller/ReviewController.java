package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.ProductDTO;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SignUserRepository signUserRepository;

    //리뷰 idx 값에서 맞는 product 정보 값을가져옴
    @GetMapping("/review/{product_buy_idx}")
    public String reviewForm(Model model , @PathVariable("product_buy_idx") Long productBuyIdx , Principal principal) {
        Review review = new Review();
        ProductDTO productDTO = new ProductDTO(); // 필요한 데이터로 초기화

        review.setReviewUser(principal.getName());
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저정보를 찾을수 없습니다"));

        Product product = this.reviewService.getReviewinfo(productBuyIdx);
        String[] product_images = product.getProduct_image().split(",");
        Product_buy productBuy = this.reviewService.getQuantity(productBuyIdx,sign.getIdx());

        model.addAttribute("product", product); // 리뷰 상품 정보 가져오기
        model.addAttribute("productbuy", productBuy); // 리뷰 상품 수량 가져오기
        model.addAttribute("product_images", product_images); // 리뷰 상품 이미지 가져오기
        model.addAttribute("review", review);
        model.addAttribute("productDTO", productDTO);

        return "review"; // templates/review.html을 찾음
    }

    //리뷰 등록하기
    @PostMapping("/review")
    public String addReview(Model model, @ModelAttribute("review") Review review,
                            @Validated @RequestParam("files") MultipartFile[] files,
                            BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            model.addAttribute("error", "입력한 값에 오류가 있습니다");
        }
        try{
            reviewService.registerReview(review, files);
            model.addAttribute("message", "리뷰가 성공적으로 등록되었습니다.");
            return "index";
        } catch (Exception e){
            model.addAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "index";
        }
    }
}
