package org.portfolio.spring_1.repository;

import org.portfolio.spring_1.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findBySerial(String serial);
}
