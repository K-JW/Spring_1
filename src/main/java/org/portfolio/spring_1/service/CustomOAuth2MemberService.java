package org.portfolio.spring_1.service;

import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.dto.GoogleResponse;
import org.portfolio.spring_1.dto.MemberDTO;
import org.portfolio.spring_1.dto.OAuth2UserResponse;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.entity.MemberRole;
import org.portfolio.spring_1.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        } else {
            return null;
        }

        // oauth2를 통해 받은 사용자 정보를 사용해 Member 생성 및 저장
        String serial = oAuth2UserResponse.getProviderId();
        String provider = oAuth2UserResponse.getProvider();
        String name = oAuth2UserResponse.getName();
        String email = oAuth2UserResponse.getEmail();

        Member findMember = memberRepository.findBySerial(serial);

        // serial로 DB 조회 시 null이면
        // 사용자가 최초로 OAuth2로 login 하는 경우
        if (findMember == null) {
            return createCustomOauth2Member(serial, provider, name, email, MemberRole.USER);
        }
        // 최초 로그인이 아닌 경우
        else return createCustomOauth2Member(findMember.getSerial(),
                provider, name, email, findMember.getMemberRole());
    }


    public CustomOAuth2Member createCustomOauth2Member(String serial, String provider, String name, String email, MemberRole memberRole) {

        Member member = Member.builder()
                .serial(serial)
                .provider(provider)
                .name(name)
                .email(email)
                .memberRole(memberRole)
                .build();

        memberRepository.save(member);

        MemberDTO memberDTO = MemberDTO.builder()
                .serial(serial)
                .name(name)
                .provider(provider)
                .role(String.valueOf(memberRole))
                .build();

        return new CustomOAuth2Member(memberDTO);
    }
}
