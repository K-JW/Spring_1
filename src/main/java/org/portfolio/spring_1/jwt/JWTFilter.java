package org.portfolio.spring_1.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.dto.MemberDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        // header에서 token 존재 여부 확인, 존재하지 않을 시 다음 필터 진행
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // token 만료 여부 확인, 만료시 필터 진행 종료
        try {
            jwtUtil.isExpired(accessToken);
        } catch(ExpiredJwtException e) {
            exceptionResponse(response,"access token expired");
        }

        // token이 access token인지 확인, 아닐시 필터 진행 종료
        String category = jwtUtil.getClaim(accessToken, "category");
        if (!category.equals("access")) {
            exceptionResponse(response, "invalid access token");
        }

        // token의 클레임 정보로 MemberDTO 생성
        MemberDTO memberDTO = createMemberDTO(accessToken);

        // MemberDTO로 OAuth2Member 객체 생성
        CustomOAuth2Member oAuth2Member = new CustomOAuth2Member(memberDTO);

        // OAuth2Member로 인증 객체 생성 및 저장
        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2Member, null, oAuth2Member.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // 예외 처리 메소드, 다음 필터를 진행하지 않고 종료한다.
    public void exceptionResponse(HttpServletResponse response, String message) throws IOException {
        response.getWriter().print(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    private MemberDTO createMemberDTO(String accessToken) {
        String serial = jwtUtil.getClaim(accessToken, "serial");
        String role = jwtUtil.getClaim(accessToken, "role");
        String name = jwtUtil.getClaim(accessToken, "name");
        String provider = jwtUtil.getClaim(accessToken, "provider");

        return MemberDTO.builder()
                .serial(serial)
                .role(role)
                .name(name)
                .provider(provider)
                .build();
    }
}
