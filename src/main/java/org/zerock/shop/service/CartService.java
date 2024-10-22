package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.dto.CartDetailDto;
import org.zerock.shop.dto.CartItemDto;
import org.zerock.shop.dto.CartOrderDto;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.entity.Cart;
import org.zerock.shop.entity.CartItem;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.CartItemRepository;
import org.zerock.shop.repository.CartRepository;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId()) // 장바구니에 담을 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);        // 현재 로그인한 회원 엔티티 조회

        Cart cart = cartRepository.findByMemberId(member.getId());  // 현재 로그인한 회원의 장바구니 엔티티 생성
        if(cart == null){       // 상품이 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()); // 현재 상품이 이미 있는지 검토

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount()); // 이미 있는 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼 더해줌.
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());  // 장바구니 엔티티, 상품엔티티, 장바구니 수량을 이용해 createCartItem 생성
            cartItemRepository.save(cartItem);  // 장바구니에 들어갈 상품을 저장
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){  // 343 추가 (로그인한 회원의 정보를 이용하여 장바구니에 들어 있는 상품을 조회)

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){ // 장바구니에 상품을 한 번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){  // 351 추가
        Member curMember = memberRepository.findByEmail(email);  // 현재 로그인한 사용자
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();  // 장바구니 상품을 저장한 회원 조회

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){  // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우
            return false;
        }

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){  // 장바구나 상품의 수량을 업데이트
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {  // 355 추가
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){ // 361 추가
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) { // 장바구니 페이지에서 전달 받은 주문 상품 번호를 이용하여 주문 로직으로 전달할 orderDto 객체를 만듬
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);    // 장바구니에 담은 상품을 주문하도록 주문 로직 호출
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {    // 주문한 상품들을 장바구니에서 제거
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}
