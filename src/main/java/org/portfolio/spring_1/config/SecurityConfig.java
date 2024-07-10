package org.portfolio.spring_1.config;

import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.jwt.CustomLogoutFilter;
import org.portfolio.spring_1.jwt.JWTFilter;
import org.portfolio.spring_1.jwt.JWTUtil;
import org.portfolio.spring_1.oauth2.CustomClientRegistrationRepo;
import org.portfolio.spring_1.oauth2.CustomFailureHandler;
import org.portfolio.spring_1.oauth2.CustomSuccessHandler;
import org.portfolio.spring_1.service.CustomOAuth2MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2MemberService customOAuth2MemberService;
    private final JWTUtil jwtUtil;
    private final JWTFilter jwtFilter;
    private final CustomLogoutFilter customLogoutFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF Disable
        http.csrf(AbstractHttpConfigurer::disable);

        // HTTP Authentication Disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Form Login Disable
        http.formLogin(AbstractHttpConfigurer::disable);

        // Logout Disable
//        http.logout(AbstractHttpConfigurer::disable);

        // JWT Filter
        http.addFilterBefore(jwtFilter, OAuth2LoginAuthenticationFilter.class);

        // OAuth2 Config
        http.oauth2Login((oauth2) -> oauth2
                .loginPage("/login")
                .successHandler(customSuccessHandler)
                .failureHandler(customFailureHandler)
                .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                .userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2MemberService)));

        // Authorize
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers( "/", "/**").permitAll()
                .anyRequest().permitAll()
        );

        // Logout
        http.addFilterBefore(customLogoutFilter, LogoutFilter.class);

        // Session Config
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web)-> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/h2-console/**")
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }
}
