package org.portfolio.spring_1.repository;

import org.portfolio.spring_1.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT m FROM Member m WHERE m.serial = :serial")
    Member findBySerial(@Param("serial") String serial);
}
