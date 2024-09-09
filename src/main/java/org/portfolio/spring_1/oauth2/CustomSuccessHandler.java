package org.portfolio.spring_1.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.jwt.JWTUtil;
import org.portfolio.spring_1.service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    private final RedisService redisService;
    private static final long ACCESS_TOKEN_EXPIRATION = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION = 600000 * 6 * 24L; // 24시간
    private static final String REDIRECT_PATH = "/articles";
    private static final String ADMIN_REDIRECT_PATH = "/admin";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("onAuthenticationSuccess method call");

        // 사용자 정보 추출
        CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();

        String serial = oAuth2Member.getSerial();
        String role = getRole(authentication.getAuthorities());
        String name = oAuth2Member.getName();
        String provider = oAuth2Member.getProvider();

        // 토큰 생성
        String accessToken = jwtUtil.createJwt("access", serial, role, name, provider, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createJwt("refresh", serial, role, name, provider, REFRESH_TOKEN_EXPIRATION);

        // refresh token은 redis를 통해 관리
        redisService.setValues(serial, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        System.out.printf("accessToken = %s%n", accessToken);

        // 응답 설정
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Authorization", "Bearer " + accessToken);
//        response.addCookie(createCookie("access", accessToken));
//        response.addCookie(createCookie("refresh", refreshToken));

        if (role.equals("USER")) {
            response.sendRedirect(REDIRECT_PATH);
        } else {
            response.sendRedirect(ADMIN_REDIRECT_PATH);
        }

    }

    private String getRole(Collection<? extends GrantedAuthority> authorities) {
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        return authority.getAuthority();
    }

    private Cookie createCookie(String token, String value) {
        Cookie cookie = new Cookie(token, value);
        int maxAge;

        if (token.equals("access")) {
            maxAge = (int) ACCESS_TOKEN_EXPIRATION / 1000;
        } else {
            maxAge = (int) REFRESH_TOKEN_EXPIRATION / 1000;
        }

        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // https 환경

        return cookie;
    }
}
