package org.portfolio.spring_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.*;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.entity.MemberRole;
import org.portfolio.spring_1.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        // 매개변수에서 provider 추출
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserResponse oAuth2UserResponse;

        if (registrationId.equals("google")) {
            oAuth2UserResponse = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            oAuth2UserResponse = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2UserResponse = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // oauth2를 통해 받은 사용자 정보를 사용해 Member 생성 및 저장
        String serial = oAuth2UserResponse.getProviderId();
        String provider = oAuth2UserResponse.getProvider();
        String name = oAuth2UserResponse.getName();
        String email = oAuth2UserResponse.getEmail();

        Member getMember = memberRepository.findBySerial(serial);

        // serial로 DB 조회 시 null이면
        // 사용자가 최초로 OAuth2로 login 하는 경우
        if (getMember == null) {
            return createCustomOauth2Member(serial, provider, name, email);
        }
        // 최초 로그인이 아닌 경우
        else return createCustomOauth2Member(getMember.getSerial(),
                provider, name, email);
    }


    public CustomOAuth2Member createCustomOauth2Member(String serial, String provider, String name, String email) {

        Member member = Member.builder()
                .serial(serial)
                .provider(provider)
                .name(name)
                .email(email)
                .memberRole(MemberRole.USER)
                .build();

        memberRepository.save(member);

        MemberDTO memberDTO = MemberDTO.builder()
                .serial(serial)
                .name(name)
                .provider(provider)
                .role(String.valueOf(MemberRole.USER))
                .build();

        CustomOAuth2Member customOAuth2Member = new CustomOAuth2Member(memberDTO);
        log.info("oauth2Member role = {}", MemberRole.USER);

        return customOAuth2Member;
    }

    // 현재 로그인 된 사용자 serial의 DB 확인 method
    public Member getMemberBySerial(String loggedInMemberSerial) {

        if (loggedInMemberSerial == null) {
            throw new IllegalArgumentException("loggedInMemberSerial is null");
        }

        Member getLoggedInMember = memberRepository.findBySerial(loggedInMemberSerial);

        if (getLoggedInMember == null) {
            throw new NoSuchElementException("해당 Serial의 Member가 DB에 존재하지 않습니다.");
        }

        log.info("memberSerial = {}", getLoggedInMember.getSerial());

        return getLoggedInMember;
    }

}
