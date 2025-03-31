package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.CartProductDTO;
import com.mysite.jjw.DTO.ProductBuyDTO;
import com.mysite.jjw.entity.Cart;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.CartService;
import com.mysite.jjw.service.ProductBuyService;
import com.mysite.jjw.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductBuyController {

    private final ProductBuyService productBuyService;
    private final CartService cartService;
    private final SignUserRepository signUserRepository;
    private final ProductRepository productRepository;


    //   product_but_productidx 구매 상품 번호
    //   useridx 구매한 유저 번호
    @PostMapping("/productBuy")
    public ResponseEntity<Map<String, Object>> buyProduct(@RequestParam("product_buy_productidx") Long productBuyProductIdx,
                                                          @RequestParam("quantity") Long quantity,
                                                          @RequestParam("action") String action,
                                                          Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Product> productOpt = productRepository.findById(productBuyProductIdx);
        Product product = productOpt.get();

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
        Long useridx = sign.getIdx();

        if (quantity == null || quantity <= 0) {
            response.put("status", "error");
            response.put("message", "수량을 선택해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            if ("cart".equals(action)) {
                cartService.addCart(productBuyProductIdx, quantity, useridx);
                response.put("status", "success");
                response.put("message", "장바구니에 추가 되었습니다.");
            } else if ("buy".equals(action)) {
                productBuyService.registerBuy(productBuyProductIdx, quantity, useridx);
                product.setProduct_sale((int) (product.getProduct_sale() + quantity));
                productRepository.save(product);
                response.put("status", "success");
                response.put("message", "구매가 완료되었습니다.");
            } else {
                response.put("status", "error");
                response.put("message", "잘못된 요청입니다.");
            }
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            response.put("status", "exists"); // 기존 "error" 대신 "exists"로 상태 변경
            response.put("message", e.getMessage()); // "이미 장바구니에 담긴 상품입니다."
            return ResponseEntity.ok(response);  // 400 -> 200 으로 변경
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다." + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
