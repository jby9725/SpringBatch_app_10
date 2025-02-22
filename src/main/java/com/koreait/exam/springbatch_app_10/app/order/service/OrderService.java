package com.koreait.exam.springbatch_app_10.app.order.service;

import com.koreait.exam.springbatch_app_10.app.cart.entity.CartItem;
import com.koreait.exam.springbatch_app_10.app.cart.service.CartService;
import com.koreait.exam.springbatch_app_10.app.member.entity.Member;
import com.koreait.exam.springbatch_app_10.app.member.service.MemberService;
import com.koreait.exam.springbatch_app_10.app.order.entity.Order;
import com.koreait.exam.springbatch_app_10.app.order.entity.OrderItem;
import com.koreait.exam.springbatch_app_10.app.order.repository.OrderItemRepository;
import com.koreait.exam.springbatch_app_10.app.order.repository.OrderRepository;
import com.koreait.exam.springbatch_app_10.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final MemberService memberService;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createFromCart(Member buyer) {
        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.
        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제
        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }
            cartService.removeItem(cartItem);
        }
        return create(buyer, orderItems);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로부터 이름 생성
        order.makeName();

        orderRepository.save(order);
        return order;
    }

    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        int payPrice = order.calculatePayPrice();
        if (payPrice > restCash) {
            throw new RuntimeException("충전금이 부족합니다.");
        }
        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__충전금".formatted(order.getId()));
        order.setPaymentDone();
        orderRepository.save(order);
    }

    @Transactional
    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__충전금".formatted(order.getId()));
        order.setRefundDone();
        orderRepository.save(order);
    }

    public Optional<Order> findForPrintById(Long id) {
        return orderRepository.findById(id);
    }

    public boolean actorCanSee(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    @Transactional
    public void payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, payPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));
        if (useRestCash > 0) {
            memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__충전금".formatted(order.getId()));
        }

        order.setPaymentDone();
        orderRepository.save(order);
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actorCanSee(actor, order);
    }

    public List<OrderItem> findAllByPayDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByPayDateBetween(fromDate, toDate);
    }
}