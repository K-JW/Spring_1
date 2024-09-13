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
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.service.RedisService;
import org.portfolio.spring_1.service.ReissueService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final ReissueService reissueService;

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

        String accessToken = request.getHeader("Authorization");

        log.info("logout's accessToken = {}", accessToken);

        String serial = jwtUtil.getClaim(accessToken, "serial");

        String refreshTokenInRedis = redisService.getValues(serial);

//        String refreshToken = null;
//        Cookie[] cookies = request.getCookies();
//
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals("refresh")) {
//                refreshToken = cookie.getValue();
//                System.out.println("refresh token initialized");
//            }
//        }

        // refresh token의 null, expiration, category, redis에 존재 여부 확인
        // 모든 확인 후 token의 serial return
        String key = reissueService.checkRefreshToken(refreshTokenInRedis, response);

        redisService.deleteValues(key);

        // 클라이언트 쿠키 값 초기화 메소드 호출
//        Cookie refresh = deleteCookie("refresh");
//        Cookie access = deleteCookie("access");
        Cookie session = deleteCookie("JSESSIONID");

        response.setStatus(HttpServletResponse.SC_OK); // == 200
//        response.addCookie(refresh);
//        response.addCookie(access);
        response.addCookie(session);

        System.out.println("redis에 존재하는 refresh token 삭제 후 login page로 이동");
        response.sendRedirect("/login");
    }

    private Cookie deleteCookie(String token) {
        Cookie cookie = new Cookie(token, null);

        cookie.setMaxAge(0);
        cookie.setPath("/");

        return cookie;
    }
}
