package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {
    // 359 추가 장바구 상품 주문하기

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;  // 장바구니에서 여러개의 상품을 주문 함

}