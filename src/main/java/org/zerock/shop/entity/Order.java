package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{

    // 회원이 주문 한 경우에 처리되는 엔티티

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;          // 주문한 사용자 상태 객체

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)

    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {// 299 추가 (주문 상품 정보를 담아 줌)
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) { // OrderItem엔티티가 양방향 참조 관계임
        Order order = new Order();
        order.setMember(member);    // 상품을 주문한 회원의 정보를 셋팅

        for(OrderItem orderItem : orderItemList) {  // 상품 페이지에서는 1개의 상품을 주문 하지만, 장바구니 페이지에는 한번에 여러개의 상품을 주문 함
            order.addOrderItem(orderItem);  // 리스트로 받음 -> 주문 객체에 orderItem 객체 추가
        }

        order.setOrderStatus(OrderStatus.ORDER);    // 주문 상태를 ORDER로 셋팅
        order.setOrderDate(LocalDateTime.now());    // 현재 주문 시간으로 셋팅
        return order;
    }

    public int getTotalPrice() {    // 총 주문 금액을 구하는 메서드
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {  // 322 주문 취소용
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


}
