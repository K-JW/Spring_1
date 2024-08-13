package org.portfolio.spring_1.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

@Component
public class SocialClientRegistration {

    public ClientRegistration googleClient() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("1068190921774-bstlom3ha1c7itj5kno8e7f4kq3i1kec.apps.googleusercontent.com")
                .clientSecret("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")
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .scope("profile", "email")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                // 선택 사항
                .issuerUri("https://accounts.google.com")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
    }

    public ClientRegistration naverClient() {
        return ClientRegistration.withRegistrationId("naver")
                .clientId("imeGTwdgMhrFUwbAq3jZ")
                .clientSecret("jQ8Sj1mmG6")
                .redirectUri("http://localhost:8080/login/oauth2/code/naver")
                .scope("email", "name")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();
    }

    public ClientRegistration kakaoClient() {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId("010ec21f0cc86fabacf6620b120d23d3")
                .clientSecret("944uM09bfAMOCmBJYqyc8midvveJpUo6")
                .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
                .scope("profile_nickname", "account_email")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                // kakao oauth2 필수 추가 항목
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .build();
    }
}
