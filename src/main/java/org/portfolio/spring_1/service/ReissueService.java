package org.portfolio.spring_1.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION = 600000 * 6 * 24L; // 24시간
    private static final int BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST; // == 400


    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response, String accessToken) {

        String getSerial = jwtUtil.getClaim(accessToken, "serial");

        String getRefresh = redisService.getValues(getSerial);

        String checkedRefresh = checkRefreshToken(getRefresh, response);

        String serial = jwtUtil.getClaim(checkedRefresh, "serial");
        String name = jwtUtil.getClaim(checkedRefresh, "name");
        String role = jwtUtil.getClaim(checkedRefresh, "role");
        String provider = jwtUtil.getClaim(checkedRefresh, "provider");
        String category = jwtUtil.getClaim(checkedRefresh, "category");

        String newAccessToken = jwtUtil.createJwt("access", serial, role, name, provider, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt(category, serial, role, name, provider, REFRESH_TOKEN_EXPIRATION);

        redisService.setValues(serial, newRefreshToken, Duration.ofMillis(86400000L));

        response.setHeader("Authorization", "Bearer " + newAccessToken);
//        response.addCookie(createCookie(category, newRefreshToken));
//        response.addCookie(createCookie("access", newAccessToken));

        return new ResponseEntity<>(HttpStatus.OK);
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

    public String checkRefreshToken(String token, HttpServletResponse response) {

        // null 확인
        if (token == null) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // expiration 확인
        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // category 확인
        String category = jwtUtil.getClaim(token, "category");

        if (!category.equals("refresh")) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        // redis에 존재 여부 확인
        String key = jwtUtil.getClaim(token, "serial");

        if (redisService.getValues(key) == null) {
            response.setStatus(BAD_REQUEST);
            return null;
        }

        return key;
    }

}
