package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.ProductDTO;
import com.mysite.jjw.entity.*;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.ProductAnswerService;
import com.mysite.jjw.service.ProductQuestionService;
import com.mysite.jjw.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Controller
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final SignUserRepository signUserRepository;
    private final ProductQuestionService productQuestionService;
    private final ProductAnswerService productAnswerService;

    //상품 상세 정보 Detail
    @GetMapping(value = "/productDetail/{product_idx}")
    // PathVariable 클라이언트가 URL에 포함 시킨 값을 받아오기
    public String productDetail(Model model, @PathVariable("product_idx") Long product_idx,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "latest") String sort,
                                Principal principal) {
        Product product = this.productService.getProductInfo(product_idx);
        List<Product_question> productQuestion = productQuestionService.getQuestionInfo(product_idx);
        Page<Review> reviewPage;

        reviewPage = productService.getReviewInfo(PageRequest.of(page, 5), sort, product_idx); // 상품 번호를 비교


        // 이미지는 , 로 구별해서 하나씩 넘겨주기
        String[] product_images = product.getProduct_image().split(",");


        // 리뷰별 이미지 저장할 Map 생성
        Map<Long, String[]> reviewImagesMap = new HashMap<>();
        // 리뷰별 등록 이름 가져오기
        Map<Long, String> reviewUserNameMap = new HashMap<>();
        // 리뷰별 등록 이름 유저 이미지 가져오기
        Map<Long, String> reviewUserImageMap = new HashMap<>();
        // QnA 유저 이름 저장할 맵
        Map<Long, String> questionUserNameMap = new HashMap<>();
        // 질문별 답변 저장할 맵
        Map<Long, String> questionAnswerMap = new HashMap<>();


        for (Review review : reviewPage) {
            String reviewImageStr = review.getReviewImage();
            if (reviewImageStr != null && !reviewImageStr.isEmpty()) {
                String[] reviewImages = reviewImageStr.split(",");
                reviewImagesMap.put(review.getReviewIdx(), reviewImages);
            } else {
                reviewImagesMap.put(review.getReviewIdx(), new String[]{});
            }

            Optional<Sign> sign = signUserRepository.findById(review.getReviewUser());
            String signName = sign.map(Sign::getName).orElse("알 수 없음"); // 유저가 존재하지 않으면 기본값 설정
            String signImage = sign.map(Sign::getMemberImage).orElse("알 수 없음"); // 유저가 존재하지 않으면 기본값 설정
            reviewUserNameMap.put(review.getReviewIdx(), signName);
            reviewUserImageMap.put(review.getReviewIdx(), signImage);
        }

        // 상품에 대한 Qna 답변 작성한 이름 보여주기
        for (Product_question product_question : productQuestion) {
            Optional<Sign> sign = signUserRepository.findById(product_question.getProductQuestionUseridx());
            questionUserNameMap.put(product_question.getProductQuestionIdx(), sign.map(Sign::getName).orElse("알 수 없음"));

            // 해당 질문에 대한 답변 조회
            Optional<Product_answer> product_answer = productAnswerService.getAnswerInfo(product_question.getProductQuestionIdx());
            questionAnswerMap.put(product_question.getProductQuestionIdx(), product_answer.map(Product_answer::getProductAnswerContent).orElse("답변이 없습니다."));
        }


        model.addAttribute("question_list", productQuestion); // Qna를 등록한 정보를 가져옴
        model.addAttribute("product_question", new Product_question()); // QnA객체를 등록함
        model.addAttribute("question_name", questionUserNameMap); // QnA 유저 이름 추가

        model.addAttribute("product_answer", new Product_answer());// 질문에 대한 답변 form으로 객체 보내기
        model.addAttribute("answer_list", questionAnswerMap);// 질문에 대한 답변 보여주기

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewPage);
        model.addAttribute("product_images", product_images);
        model.addAttribute("reviewUserName", reviewUserNameMap);
        model.addAttribute("reviewUserImage", reviewUserImageMap);
        model.addAttribute("reviewimages", reviewImagesMap); // 리뷰 이미지 리스트 추가

        return "productDetail";
    }

    // 상품 목록 조회
    @GetMapping("/product_list")
    public String productList(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "latest") String sort,
                              @RequestParam(required = false) String search) {

        // sort 값이 잘못 들어왔을 경우 기본값 설정
        if (sort == null || sort.isEmpty()) {
            sort = "latest"; // 기본값 설정
        }

        Page<Product> productPage;

        // 검색어가 있으면 검색 기능 수행
        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProduct(search, sort , PageRequest.of(page, 15));
        } else {
            // 검색어가 없으면 모든 상품 목록 조회
            productPage = productService.getAllProducts(PageRequest.of(page, 15),sort);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("sort", sort);
        model.addAttribute("search", search); // 검색어 유지

        return "product_list";
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "product"; // 에러가 발생한 페이지로 돌아감
    }

    // 상품 정보 가져오기
    @GetMapping("/product")
    public String productForm(Model model, Principal principal) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProduct_user(principal.getName()); // 현재 로그인한 사용자 이름 설정
        model.addAttribute("productDTO", productDTO); // 모델에 추가
        return "product";
    }

    //상품 등록하기
    @PostMapping("/product")
    public String addProduct(@ModelAttribute("productDTO")@Validated ProductDTO productDTO,
                             BindingResult bindingResult,
                             @Validated @RequestParam("files") MultipartFile[] files,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "입력한 값에 오류가 있습니다. 다시 확인해주세요.");
            return "product";  // 오류 시 product 페이지로 다시 돌아감
        }

        try {
            productService.registerProduct(productDTO, files); // 상품 등록 처리
            redirectAttributes.addFlashAttribute("message", "상품 등록이 완료되었습니다.");
            return "redirect:/"; // 성공 시 메인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "product"; // 오류 발생 시 다시 상품 페이지로 돌아감
        }
    }
}
