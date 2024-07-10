package org.portfolio.spring_1.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private static final long ACCESS_TOKEN_EXPIRATION = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION = 600000 * 6 * 24L; // 24시간
    private static final String REDIRECT_PATH = "/articles";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

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

        // 응답 설정
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie(refreshToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(REDIRECT_PATH);
    }

    private String getRole(Collection<? extends GrantedAuthority> authorities) {
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        return authority.getAuthority();
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refresh", value);

        cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRATION / 1000);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // https 환경

        return cookie;
    }
}
