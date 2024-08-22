package org.portfolio.spring_1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member {

    @Id
    @Column(name = "serial", unique = true)
    private String serial;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "provider")
    private String provider;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;
}
