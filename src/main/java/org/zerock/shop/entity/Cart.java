package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)  // 1:1 매핑 (회원과 카트)
    @JoinColumn(name="member_id")   // 조인은 회원의 id와(매핑할 외래키 foreign key(member_id) references member )
    private Member member;

    public static Cart createCart(Member member){  // 331 추가 장바구니 용
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}