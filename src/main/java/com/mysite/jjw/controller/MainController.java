package com.mysite.jjw.controller;

import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.SignUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@ControllerAdvice //모든 컨트롤러에 적용되는 공통 기능 제공
@RequiredArgsConstructor
public class MainController {

    private final SignUserRepository signUserRepository;
    private final ProductRepository productRepository;

    @GetMapping("/")
    public String index(Model model) {

        LocalDate oneWeekAgo = LocalDate.now().minusDays(30); // LocalDate 사용
        Pageable pageable = PageRequest.of(0, 10);

        List<Product> bestSellingProducts = productRepository.findTop10BestSelling(oneWeekAgo, pageable);
        List<Product> newProducts = productRepository.findTop10NewProducts(oneWeekAgo, pageable);

        model.addAttribute("bestSellingProducts", bestSellingProducts);
        model.addAttribute("newProducts", newProducts);

        return "index"; // templates/index.html을 찾음
    }



    // 유저 정보 가져오기 이름
    @ModelAttribute
    public void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 현재 로그인한 사용자 정보 가져오기

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {

            User principal = (User) authentication.getPrincipal(); // 현재 로그인한 사용자
            String userId = principal.getUsername(); // 사용자 ID 가져오기

            // DB에서 userId에 해당하는 Sign 정보 가져오기
            Optional<Sign> optionalSign = signUserRepository.findById(userId);
            String realName = optionalSign.map(Sign::getName).orElse(""); // 이름 가져오기
            model.addAttribute("name", realName); // 실제 이름 추가
            model.addAttribute("userid", optionalSign.map(Sign::getId).orElse(null));

            // 권한 목록 가져오기 (ROLE)
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER"); // 기본값 (로그인 안 했거나 ROLE이 없을 때)

            model.addAttribute("role", role); // ROLE 추가
        } else {
            model.addAttribute("name", ""); // 로그인 안 했을 때 빈 값
            model.addAttribute("role", "ROLE_GUEST"); // 기본 권한
        }
    }




}