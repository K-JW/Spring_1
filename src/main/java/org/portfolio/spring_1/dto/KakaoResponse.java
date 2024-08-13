package org.portfolio.spring_1.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoResponse implements OAuth2UserResponse {

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return ((Map)attribute.get("properties")).get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return ((Map) attribute.get("kakao_account")).get("email").toString();
    }

}
