package org.zerock.shop.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 182 AJAX인 경우 http request header에 XMLHttpRequest라는 값이 셋팅되어 요청이 오는데
    // 인증되지 않은 사용자가 AJAX로 요쳥시 Unauthorized 에러가 나옴, 나머지 경우 로그인 페이지로 리다이렉트 시켜줌.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

}