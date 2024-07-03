package org.portfolio.spring_1.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

@Component
public class SocialClientRegistration {

    public ClientRegistration googleClient() {
        return ClientRegistration.withClientRegistration("google")
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
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userInfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
    }
}
