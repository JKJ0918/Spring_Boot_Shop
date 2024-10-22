package org.zerock.shop.exception;

public class OutOfStockException extends RuntimeException{
    // 상품의 주문 수량보다 재고수가 작을때 발생 시키는 예외 처리

    public OutOfStockException(String message) {

        super(message);
    }

}