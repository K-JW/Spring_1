package org.portfolio.spring_1.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2Member implements OAuth2User {

    private final MemberDTO memberDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) memberDTO::getRole);

        return collection;
    }

    public String getSerial() {
        return memberDTO.getSerial();
    }

    public String getProvider() {
        return memberDTO.getProvider();
    }

    @Override
    public String getName() {
        return memberDTO.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
}
