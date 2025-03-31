package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.CartService;
import com.mysite.jjw.service.OrderService;
import com.mysite.jjw.service.ProductBuyService;
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
public class OrderController {

    private final OrderService orderService;
    private final SignUserRepository signUserRepository;


    //구매 목록에서 검색 기능
    @GetMapping("/order")
    public String order(Model model, Principal principal, @RequestParam(required = false) String search) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));

        // 기존 상품 정보
        List<ProductBuyDTO> buyItems = orderService.getBuyItems(sign.getIdx(),search);

        for (ProductBuyDTO item : buyItems) {
            boolean hasReviewed = orderService.hasUserReviewedProduct(sign.getIdx(), item.getProduct().getProduct_idx() , item.getProductBuy().getProductBuyIdx());
            item.setHasReviewed(hasReviewed); // hasReviewed 값을 DTO에 추가

        }

        model.addAttribute("buyItems", buyItems); // DTO 리스트 전달
        model.addAttribute("search", search);

        return "order";
    }

    // 마이페이지에 총구매 전체 가격 및 전체 수량 나타내기
    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));

        String UserImage = sign.getMemberImage();

        Map<String, Integer> totalData = orderService.getTotalPriceAndQuantity(sign.getIdx());

        model.addAttribute("totalPrice", totalData.get("totalPrice"));
        model.addAttribute("UserImage", UserImage);
        model.addAttribute("totalQuantity", totalData.get("totalQuantity"));

        return "mypage";
    }

    // 배송완료 였던 상품 구매 확정
    @PostMapping("/order/completed")
    public ResponseEntity<Map<String, Object>> completedOrder(@RequestBody Map<String, Object> requestData) {
        Long productBuyProductIdx = Long.parseLong(requestData.get("productBuyProductIdx").toString());
        Map<String, Object> response = new HashMap<>();

        try {
            // 서비스 호출하여 주문 취소 처리
            boolean cancelSuccess = orderService.productCompleted(productBuyProductIdx);

            if (cancelSuccess) {
                response.put("status", "success");
                response.put("message", "상품 구매를 확정했습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "fail");
                response.put("message", "상품을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("message", "구매를 확정하는 도중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
