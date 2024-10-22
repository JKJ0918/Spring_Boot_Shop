package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.dto.OrderHistDto;
import org.zerock.shop.service.OrderService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal){
        // 스프링에서 비동기 처리할 때 @RequestBody 와 @ResponseBody 를 사용함
        // @RequestBody : Http 요청의 본문 body에 담긴 내용을 자바 객체로 전달
        // @ResponseBody : 자바 객체를 http 요청의 body로 전달

        if(bindingResult.hasErrors()){ // orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사.
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); // 에러 정보를 ResponseEntity 객체에 담아 반환
        }

        String email = principal.getName(); // 로그인 유저의 정보를 얻어 이름을 알아옴(이메일)
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);  // 화면으로 넘어오는 주문 정보과 회원 이메일 정보를 이용하여 주문 로직을 호출
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK); // 정상처리
    }

    @GetMapping(value = {"/orders", "/orders/{page}"}) // 314 추가 (주문 이력 조회용)
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        // 한번에 가져올 주문 개수 4개
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        // 현재 로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달, 화면에 주문목록 데이터를 리턴 값으로 받음.
        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";  // order/orderHist.html로 리턴
    }

    @PostMapping("/order/{orderId}/cancel") // 324 주문 취소용 메서드 (비동기 처리)
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } // 다른 사람이 url로 주문 취소 못하도록 설정

        orderService.cancelOrder(orderId);  // 주문 취소 로직 실행
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
