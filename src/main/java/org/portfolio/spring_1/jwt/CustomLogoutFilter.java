package org.portfolio.spring_1.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.service.RedisService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private static final int BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST; // == 400

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 요청 경로 확인
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);

        if (!requestURI.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 HTTP Method 확인
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 refresh token 확인, 존재한다면 변수에 할당
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
                System.out.println("refresh token initialized");
            }
        }

        // refresh token의 null, expiration, category, redis에 존재 여부 확인
        // 모든 확인 후 token의 serial return
        String key = checkRefreshToken(refreshToken, response);

        redisService.deleteValues(key);

        // 클라이언트 쿠키 값 초기화 메소드 호출
        Cookie refresh = deleteCookie("refresh");
        Cookie access = deleteCookie("access");
        Cookie session = deleteCookie("JSESSIONID");

        response.setStatus(HttpServletResponse.SC_OK); // == 200
        response.addCookie(refresh);
        response.addCookie(access);
        response.addCookie(session);

        System.out.println("redis에 존재하는 refresh token 삭제 후 login page로 이동");
        response.sendRedirect("/login");
    }

    private String checkRefreshToken(String refreshToken, HttpServletResponse response) {

        // null 확인
        if (refreshToken == null) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // expiration 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // category 확인
        String category = jwtUtil.getClaim(refreshToken, "category");

        if (!category.equals("refresh")) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // redis에 존재 여부 확인
        String key = jwtUtil.getClaim(refreshToken, "serial");

        if (redisService.getValues(key) == null) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        return key;
    }

    private Cookie deleteCookie(String token) {
        Cookie cookie = new Cookie(token, null);

        cookie.setMaxAge(0);
        cookie.setPath("/");

        return cookie;
    }
}
