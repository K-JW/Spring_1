package org.portfolio.spring_1.dto;

public interface OAuth2UserResponse {

    // google, naver 등
    String getProvider();

    // 제공자가 발급해주는 ID, Member의 Serial로 사용
    String getProviderId();

    String getName();

    String getEmail();
}
