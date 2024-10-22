package org.zerock.shop.service;

import org.zerock.shop.constant.ItemSellStatus;
// import org.zerock.shop.constant.OrderStatus;
import org.zerock.shop.constant.OrderStatus;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Member;
import org.zerock.shop.entity.Order;
import org.zerock.shop.entity.OrderItem;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);

    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10); // 주문 상품 수량
        orderDto.setItemId(item.getId()); // 주문 상품

        Long orderId = orderService.order(orderDto, member.getEmail());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        int totalPrice = orderDto.getCount()*item.getPrice();

        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder(){
        Item item = saveItem(); // 상품 데이터 생성
        Member member = saveMember(); // 회원 데이터 생성

        OrderDto orderDto = new OrderDto(); 
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto, member.getEmail()); // 주문 데이터 생성, 주문 개수는 총 10개

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new); // 생성한 주문 엔티티 조회
        orderService.cancelOrder(orderId); // 해당 주문을 취소

        assertEquals(OrderStatus.CANCEL, order.getOrderStatus()); // 주문의 상태가 취소 상태면, 테스트 통과
        assertEquals(100, item.getStockNumber()); // 취소 후 상품의 재고가 처음 재고 개수와 동일하면 테스트 통과
    }

}