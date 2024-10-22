package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
}
