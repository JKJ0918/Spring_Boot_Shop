package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class OrderDto {
    // 상품 상세 페이지에서 주문할 상품의 아이디와 주문 수량을 전달 받은 객체 (최소 주문 수량은 1개, 최대는 999개)
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;

}