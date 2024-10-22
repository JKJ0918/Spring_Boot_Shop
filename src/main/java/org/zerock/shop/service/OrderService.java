package org.zerock.shop.service;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.entity.*;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.zerock.shop.dto.OrderHistDto;
import org.zerock.shop.dto.OrderItemDto;
import org.zerock.shop.repository.ItemImgRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final ItemRepository itemRepository;  // 아이템 리포지토리 CRUD

    private final MemberRepository memberRepository;  // 회원 리포지토리 CRUD

    private final OrderRepository orderRepository;  // 주문 리포지토리 CRUD

    private final ItemImgRepository itemImgRepository;  // 아이템이미지 리포지토리 CRUD


    public Long order(OrderDto orderDto, String email){
        // 주문자의 이메일과 오더를 받아 아이템을 찾음.
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();  // 주문자의 주문이 다수임으로 리스트로 처리함.
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)  // 313 추가 (OrderControll에서 호출 됨)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable); // 유저의 아이디와 페이징 조건을 이용하여 주문 목록 조회
        Long totalCount = orderRepository.countOrder(email); // 유저의 총 주문 개수

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {    // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y"); // 주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto); 
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount); //페이지 구현 객체를 생성하여 반환
    }

    @Transactional(readOnly = true)  // 324 추가
    public boolean validateOrder(Long orderId, String email){  // 현재 로그인 사용자와 주문 데이터를 생성한 사용자가 같은지 검사
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){  // 주문 취소용 메서드
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder(); // 주문 취소 상태로 변경하면 변경 감지 기능에 의해서 트랜젝션이 끝날 때 update 쿼리가 실행됨
    }

    public Long orders(List<OrderDto> orderDtoList, String email){
        // 361 추가
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) { // 주문할 상품 리스트 생성
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList); // 현재 로그인한 회원과 주문 상품 목록을 이용하여 주문 엔티티를 만듬
        orderRepository.save(order); // 주문 데이터 저장

        return order.getId();
    }

}
