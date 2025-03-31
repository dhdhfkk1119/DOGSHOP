package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.CartProductDTO;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SignUserRepository signUserRepository;

    // Cart에 담긴 정보 가져오기
    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));

        List<CartProductDTO> cartItems = cartService.getCartItems(sign.getIdx());

        model.addAttribute("cartItems", cartItems); // DTO 리스트 전달

        return "cart";
    }
    
    // 장바구니 다중 삭제
    @PostMapping("/cart/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCartItems(@RequestBody List<Long> cartIds, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            cartService.deleteCartItems(cartIds);
            response.put("status", "success");
            response.put("message", "선택한 항목이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 장바구니 단일 삭제
    @PostMapping("/cart/deleteOne")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCartItem(@RequestParam("cartIdx") Long cartIdx, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            cartService.deleteCartItem(cartIdx);
            response.put("status", "success");
            response.put("message", "상품이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 장바구니 옵션 수량 변경
    @PostMapping("/cart/option")
    public ResponseEntity<Map<String, Object>> updateCartOption(@RequestBody Map<String, Object> requestData) {
        Long cartIdx = Long.parseLong(requestData.get("cartidx").toString());
        Long quantity = Long.parseLong(requestData.get("quantity").toString());

        // 수량이 0 이하로 설정되지 않도록 처리
        if (quantity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "수량은 1 이상이어야 합니다."));
        }

        // 장바구니 번호에 해당하는 상품의 수량을 변경
        try {
            boolean updateSuccess = cartService.optionCartItem(cartIdx, quantity);

            Map<String, Object> response = new HashMap<>();
            if (updateSuccess) {
                response.put("status", "success");
                response.put("message", "장바구니가 성공적으로 업데이트되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "수량 업데이트에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "장바구니 항목을 찾을 수 없습니다."));
        }
    }

    // 장바구니에서 선택된 상품을 구매하는 기능
    @PostMapping("/cart/buy")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buySelectedItems(@RequestBody List<Long> selectedCartIds, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다"));
        Long useridx = sign.getIdx();
        System.out.println("상품 번호 = " + selectedCartIds + "유저 번호 = " + useridx );
        try {

            // 구매 처리 로직 (주문 생성 등)
            cartService.processPurchase(selectedCartIds, useridx);

            // 장바구니에서 선택된 항목 삭제
            cartService.deleteCartItems(selectedCartIds);

            response.put("status", "success");
            response.put("message", "선택한 상품이 구매되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "구매 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
