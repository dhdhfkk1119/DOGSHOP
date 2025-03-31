package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.ProductLikeProductDTO;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import com.mysite.jjw.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserLikeController {
    private final UserLikeService userLikeService;
    private final SignUserRepository signUserRepository;

    @GetMapping("/user_like")
    public String userLike(Model model, Principal principal,
                           @RequestParam(required = false , defaultValue = "latest") String sort) {

        String username = principal.getName();
        Sign sign = signUserRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("해당 유저의 정보를 찾을 수없습니다"));


        List<ProductLikeProductDTO> userLikes = userLikeService.getUserLikes(sign.getIdx(),sort);

        // 모델에 추가
        model.addAttribute("userLikes", userLikes);

        return "user_like";
    }
}
