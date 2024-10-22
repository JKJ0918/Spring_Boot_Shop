package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.CartDetailDto;
import org.zerock.shop.dto.CartItemDto;
import org.zerock.shop.dto.CartOrderDto;
import org.zerock.shop.service.CartService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){  // 장바구니에 담을 상품 정보를 받는 CartItemDto 객체에 데이터 바인딩 시 에러가 있는지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();  // 로그인한 회원이 이메일 정보를 변수에 저장
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);  // 화면으로 넘어온 장바구니에 담을 상품 정보와 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담음.
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);  // ajax 오류 처리
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK); // ajax 정상 처리
    }

    @GetMapping(value = "/cart")  // 343 추가 (장바구니 페이지로 이동하는 페이지)
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName()); // 현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보 조회
        model.addAttribute("cartItems", cartDetailList); // 조회한 장바구니 상품 정보를 뷰로 전달
        return "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")  // 353 추가 *HTTP 메소드에서 PATCH는 업데이트 할 때 PATCH사용
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){  // 수정 권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count); // 장바구니 상품의 개수를 업데이트
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}") // 355 추가
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders") // 363 추가
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){ // 주문할 상품을 선택하지 않았는지 체크
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {   // 주문 권한 체크
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName()); // 주문 로직 호출 결과 생성된 주문 번호를 반환 받음
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);    // 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드 반환
    }

}
