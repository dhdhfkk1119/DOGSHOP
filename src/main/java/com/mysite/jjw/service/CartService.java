package com.mysite.jjw.service;

import com.mysite.jjw.DTO.CartProductDTO;
import com.mysite.jjw.Handler.DataNotFoundException;
import com.mysite.jjw.entity.Cart;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.CartRepository;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final SignUserRepository signUserRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductBuyRepository productBuyRepository;

    // 장바구니 정보 가져오기
    public List<CartProductDTO> getCartItems(Long useridx) {
        List<Cart> cartItems = cartRepository.findByCartUseridx(useridx); // 유저의 장바구니 목록 가져오기
        List<CartProductDTO> cartProductDTOs = new ArrayList<>();

        for (Cart cart : cartItems) {
            Optional<Product> product = productRepository.findById(cart.getCartProductidx()); // 상품 정보 조회
            product.ifPresent(p -> cartProductDTOs.add(new CartProductDTO(cart, p)));
        }

        return cartProductDTOs; // DTO 리스트 반환
    }

    // 장바구니 구매 기능     
    public void processPurchase(List<Long> selectedCartIds, Long useridx) {

        List<Cart> selectedCarts = cartRepository.findByCartUseridx(useridx); // 장바구니에 유저 번호

        if (selectedCarts.isEmpty()) {
            throw new RuntimeException("선택한 유저가 존재 않습니다");
        }

        for (Cart cart : selectedCarts) {
            Product_buy productBuy = new Product_buy();
            productBuy.setProductBuyUseridx(useridx);
            productBuy.setProductBuyProductidx(cart.getCartProductidx());
            productBuy.setProduct_buy_quantity(cart.getCartQuantity());
            productBuy.setProduct_buy_at(LocalDateTime.now());
            productBuy.setProductStatus("delivery");

            Product product = productRepository.findById(cart.getCartProductidx())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));
            if(product.getProduct_pcs() < cart.getCartQuantity()){
                throw new RuntimeException("재고가 부족한 상품이 있습니다: " + product.getProductName());
            }
            //Product에서 갯수 구매 수량만큼 감소             
            product.setProduct_pcs((int) (product.getProduct_pcs() - cart.getCartQuantity()));

            //Product에서 판매 순위 구매 수량만큼 증가
            product.setProduct_sale((int) (product.getProduct_sale() + cart.getCartQuantity()));

            productRepository.save(product);  // 상품 재고 업데이트

            // 구매 내역 저장
            productBuyRepository.save(productBuy);

        }
        cartRepository.deleteAllById(selectedCartIds);

    }



    // 장바구니 다중 삭제
    @Transactional
    public void deleteCartItems(List<Long> cartIds) {
        cartRepository.deleteAllById(cartIds);
    }
    // 장바구니 단일 삭제
    @Transactional
    public void deleteCartItem(Long cartIdx) {
        cartRepository.deleteById(cartIdx);
    }

    // 장바구니 옵션 변경
    @Transactional
    public boolean optionCartItem(Long cartIdx , Long cartQuantity){
        // 장바구니 번호에 해당하는 상품 찾기
        Cart cart = cartRepository.findById(cartIdx)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 장바구니 수량 업데이트
        cart.setCartQuantity(cartQuantity);

        // DB에 저장
        cartRepository.save(cart);

        // 업데이트 성공 여부 리턴
        return true;
    }
    
    // 장바구니에 있는지 유무 검사
    @Transactional
    public Cart addCart(Long cart_productidx , Long cart_quantity , Long cart_useridx) {

        Product product = productRepository.findById(cart_productidx)
                .orElseThrow(() -> new RuntimeException("상품을 찾을수 없습니다"));

        Sign sign = signUserRepository.findById(cart_useridx)
                .orElseThrow(() -> new RuntimeException("회원을 찾을수 없습니다."));

        Optional<Cart> existingCart = cartRepository.findByCartUseridxAndCartProductidx(cart_useridx,cart_productidx);


        if (cart_quantity <= 0) {
            throw new RuntimeException("수량은 1개 이상이어야 합니다.");
        }
        if (existingCart.isPresent()) {
            // 기존 상품이 이미 장바구니에 있음
            throw new IllegalStateException("이미 장바구니에 담긴 상품입니다."); // IllegalStateException을 던지도록 수정
        } else {
            // 새로 추가
            Cart cart = new Cart();
            cart.setCartProductidx(cart_productidx);
            cart.setCartQuantity(cart_quantity);
            cart.setCartUseridx(cart_useridx);
            cart.setCartAt(LocalDateTime.now());
            return cartRepository.save(cart);
        }
    }
}
