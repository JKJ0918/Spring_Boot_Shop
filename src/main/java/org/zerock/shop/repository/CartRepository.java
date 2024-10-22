package org.zerock.shop.repository;

import org.zerock.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByMemberId(Long memberId);  // 332추가 현재 로그인한 회원이 Cart 엔티티를 찾음

}