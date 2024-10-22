package org.zerock.shop.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity {
    // 고객이 관심 있거나 나중에 사려는 상품들을 담아줌
    // 하나의 장바구니에는 여러개의 상품이 들어갈 수 있다.
    // 같은 상품을 여러개 주문할 수도 있다.(몇개를 담아 줄 것인지도 설정 필요)

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 장바구니에는 여러 개의 상품을 담알 수 있다.
    @JoinColumn(name="cart_id")         // foreign key (cart_id) references cart(card_id)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 상품은 여러 장바구니에 장바구니 상품으로 담김.
    @JoinColumn(name = "item_id")       // 장바구니에 담을 상품의 정보를 알아야 함
    private Item item;                  // foreign key (item_id) references item(item_id)

    private int count; // 같은 상품을 몇개 담을 지 지정

    public static CartItem createCartItem(Cart cart, Item item, int count) {  // 331 추가 장바구니용
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count){

        this.count += count;
    }  // 장바구니에 기존 담겨 있는 상품인데, 해당 상품을 추가한경우 누적

    public void updateCount(int count){  // 351 추가
        this.count = count;
    }

}