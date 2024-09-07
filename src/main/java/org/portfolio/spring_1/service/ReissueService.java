package org.portfolio.spring_1.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.jwt.JWTFilter;
import org.portfolio.spring_1.jwt.JWTUtil;
import org.portfolio.spring_1.oauth2.CustomSuccessHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION = 600000 * 6 * 24L; // 24시간

    private final JWTUtil jwtUtil;
    private final JWTFilter jwtFilter;
    private final RedisService redisService;
    private final CustomSuccessHandler customSuccessHandler;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        String checkedRefresh = jwtFilter.checkRefreshToken(refresh, response);

        String serial = jwtUtil.getClaim(refresh, "serial");
        String name = jwtUtil.getClaim(refresh, "name");
        String role = jwtUtil.getClaim(refresh, "role");
        String provider = jwtUtil.getClaim(refresh, "provider");
        String category = jwtUtil.getClaim(refresh, "category");

        String newAccessToken = jwtUtil.createJwt("access", serial, role, name, provider, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt(category, serial, role, name, provider, REFRESH_TOKEN_EXPIRATION);

        redisService.setValues(serial, newRefreshToken, Duration.ofMillis(86400000L));

        response.addCookie(createCookie("access", newAccessToken));
        response.addCookie(createCookie(category, newRefreshToken));
    }

    private Cookie createCookie(String token, String value) {

        String tokenName = null;

        if (token.equals("access")) {
            tokenName = "access";
        } else if (token.equals("refresh")) {
            tokenName = "refresh";
        }

        Cookie tokenCookie;

        tokenCookie = new Cookie(tokenName, value);

        tokenCookie.setMaxAge(24 * 60 * 60);
        tokenCookie.setHttpOnly(true);

        return tokenCookie;
    }

}
