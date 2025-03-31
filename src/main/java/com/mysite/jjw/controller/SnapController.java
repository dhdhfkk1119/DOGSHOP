package com.mysite.jjw.controller;

import com.mysite.jjw.entity.*;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.SnapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SnapController {

    private final SignUserRepository signUserRepository;
    private final SnapService snapService;

    private final String uploadURL = "C:\\Project\\jjw\\src\\main\\resources\\static\\profile-images"; // 이미지 업로드 경로
    
    //snap 에 필요한 정보 가져오기
    @GetMapping("/snap")
    public String snap(Model model, Principal principal) {
        if(principal == null){
            return "redirect:/login";
        }

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));
        String UserImage = sign.getMemberImage();

        LocalDateTime CreatAt = sign.getMemberDate().atStartOfDay();

        List<Review> reviewList = snapService.getReviewInfo(sign.getId());

        Map<Long,String[]> reviewImageMap = new HashMap<>();
        Map<Long,String[]> QuestionProductMap = new HashMap<>();
        Map<Long,String> QuestionAnswerMap = new HashMap<>();


        for(Review review : reviewList){
            String reviewImageStr = review.getReviewImage();
            if(reviewImageStr != null && !reviewImageStr.isEmpty()){
                String[] reviewImages = reviewImageStr.split(",");
                reviewImageMap.put(review.getReviewIdx(),reviewImages);
            }else{
                reviewImageMap.put(review.getReviewIdx(), new String[]{});
            }
        }

        List<Product_question> questions = snapService.getquestionList(sign.getIdx());


        // 질문 ID별 상품 정보 매핑 (상품명 + 이미지)
        for (Product_question question : questions) {
            Optional<Product> productOptional = snapService.getQuestionProduct(question.getProductQuestionProductidx());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                String[] productData = new String[2];
                productData[0] = product.getProductName(); // 상품명
                productData[1] = product.getProduct_image().split(",")[0]; // 첫 번째 이미지만 저장
                QuestionProductMap.put(question.getProductQuestionIdx(), productData);
            } else {
                QuestionProductMap.put(question.getProductQuestionIdx(), new String[]{"알 수 없는 상품", "default.jpg"});
            }

            Optional<Product_answer> answer = snapService.getAnswerList(question.getProductQuestionIdx());
            QuestionAnswerMap.put(question.getProductQuestionIdx() , answer.map(Product_answer::getProductAnswerContent).orElse("답변이 없습니다"));
        }


        model.addAttribute("reviewList", reviewList); // 로그인한 유저의 리뷰글 가져오기
        
        model.addAttribute("questions",questions); // 로그인한 유저의 Qna 정보가져오기
        model.addAttribute("QuestionAnswerMap",QuestionAnswerMap); // QnA에 대한 답변 가져오기
        
        model.addAttribute("reviewimages", reviewImageMap); // 리뷰 이미지 리스트 추가
        model.addAttribute("questionProductMap", QuestionProductMap); // 질문 ID별 상품 정보 추가
        model.addAttribute("UserImage", UserImage); // 유저 이미지 가져오기
        model.addAttribute("CreatAt", CreatAt);
        return "snap"; // templates/snap.html을 찾음
    }
    
    // 마이페이지 snap 이름 변경
    @PostMapping("/snap/rename")
    public ResponseEntity<Map<String, Object>> updateRename(@RequestBody Map<String, Object> requestData,Principal principal) {
        String rename = requestData.get("rename").toString();

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));

        if (rename == username ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "전에 이름과 동일합니다."));
        }
        else if (rename.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "이름을 입력해 주시기 바랍니다"));
        }

        // 장바구니 번호에 해당하는 상품의 수량을 변경
        try {
            boolean updateSuccess = snapService.snapRename(sign.getIdx(),rename);

            Map<String, Object> response = new HashMap<>();
            if (updateSuccess) {
                response.put("status", "success");
                response.put("message", "이름이 성공적으로 변경되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "이름 변경에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "유저 이름을 찾을 수 없습니다."));
        }
    }
    
    // 이미지 파일 변경하기
    @PostMapping("/snap/newProfileImage")
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            @RequestParam("file") MultipartFile file, Principal principal) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "이미지를 선택해주세요."));
        }

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));

        try {
            // 파일 저장 경로 설정
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadURL, fileName);

            // 파일 저장
            Files.write(filePath, file.getBytes());

            // DB에 이미지 경로 저장
            sign.setMemberImage(fileName);
            signUserRepository.save(sign);

            return ResponseEntity.ok(Map.of("status", "success", "message", "이미지가 변경되었습니다."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "파일 업로드 실패"));
        }
    }

}
