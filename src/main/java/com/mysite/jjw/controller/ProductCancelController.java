package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.OrderService;
import com.mysite.jjw.service.ProductCancelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class ProductCancelController {

    private final ProductCancelService productCancelService;
    private final SignUserRepository signUserRepository;


    //상품 캔슬(취소)한 목록 가져오기
    @GetMapping("/cancel")
    public String cancel(Model model, Principal principal , @RequestParam(required = false) String search) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));

        List<ProductBuyDTO> buyItems = productCancelService.getCancel(sign.getIdx(),search);

        model.addAttribute("buyItems", buyItems); // DTO 리스트 전달

        return "cancel";
    }

    // 배송완료 였던 주문 취소 하기
    @PostMapping("/order/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestBody Map<String, Object> requestData) {
        Long productBuyProductIdx = Long.parseLong(requestData.get("productBuyProductIdx").toString());
        Map<String, Object> response = new HashMap<>();

        try {
            // 서비스 호출하여 주문 취소 처리
            boolean cancelSuccess = productCancelService.cancelOrder(productBuyProductIdx);

            if (cancelSuccess) {
                response.put("status", "success");
                response.put("message", "상품이 취소되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "fail");
                response.put("message", "상품을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("message", "취소 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
