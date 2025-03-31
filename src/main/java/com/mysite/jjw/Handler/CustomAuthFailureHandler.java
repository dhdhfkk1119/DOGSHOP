package com.mysite.jjw.Handler;

import com.mysite.jjw.SecurityConfig.SignSecurityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다."; // 기본 오류 메시지

        if (exception instanceof SignSecurityService.CustomUserNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 틀렸습니다.";
        }

        response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}

