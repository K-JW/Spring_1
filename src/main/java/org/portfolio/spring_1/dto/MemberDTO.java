package org.portfolio.spring_1.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDTO {
    private String serial;
    private String role;
    private String name;
    private String provider;
}
