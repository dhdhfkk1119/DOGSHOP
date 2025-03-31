package com.mysite.jjw.controller;

import com.mysite.jjw.DTO.SignDTO;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.service.SignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        HttpServletRequest request, Model model) {
        if (error != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                AuthenticationException ex =
                        (AuthenticationException) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                if (ex != null) {
                    model.addAttribute("errorMessage", ex.getMessage());
                }
            }
        }
        return "login"; // login.html 반환
    }


    // ✅ 중복 확인 API (GET)
    @GetMapping("/checkIdDuplicate")
    @ResponseBody
    public Map<String, Boolean> checkIdDuplicate(@RequestParam String id) {
        boolean exists = signService.isIdExists(id); // 중복 확인
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;  // JSON 형식으로 응답
    }

    // 회원가입 object 객체 등록
    @GetMapping("/sign")
    public String signPage(Model model) {
        model.addAttribute("signDTO", new SignDTO());
        return "sign"; // templates/sign.html을 찾음
    }

    // 회원가입 등록
    @PostMapping("/sign")
    public String register(@Valid SignDTO signDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sign"; // 유효성 검사 실패 시 다시 회원가입 페이지로 이동
        }

        if (!signDTO.getPassword().equals(signDTO.getRepassword())) {
            bindingResult.rejectValue("repassword", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "sign"; // 비밀번호 불일치 시 다시 회원가입 페이지로 이동
        }

        try {
            signService.registerSign(signDTO); // SignDTO만 전달
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/login"; // 회원가입 성공 후 로그인 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // 오류 메시지를 model에 추가
            return "sign"; // 아이디나 이메일 중복 등 오류 발생 시
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "이미 등록된 사용자입니다.");
            return "sign"; // 데이터 무결성 오류 (중복된 값)
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "sign"; // 기타 예외 처리
        }
    }

}
