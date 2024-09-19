package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.order.repository.OrderItemRepository;
import com.sparta.outsouringproject.order.repository.OrderRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * 주문 생성
     *
     * @param orderRequest
     */
    public OrderCreateResponseDto createOrder(User user, OrderCreateRequestDto orderRequest) {
        if (orderRequest.getCarts().isEmpty()) {
            throw new IllegalArgumentException("장바구니에 상품이 존재하지 않습니다.");
        }

        Store store = storeRepository.findById(orderRequest.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Order order = Order.builder()
            .user(user)
            .store(store)
            .status(OrderStatus.WAITING)
            .build();

        order = orderRepository.save(order);

        // 주문 생성 후 주문 상세 테이블 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemInfo cartItemInfo : orderRequest.getCarts()) {
            Long menuId = cartItemInfo.getMenuId();
            Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

            OrderItem orderItem = new OrderItem(order, menu, cartItemInfo.getQuantity(),
                cartItemInfo.getPrice(), cartItemInfo.getTotalPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        // response dto 생성
        List<OrderItemInfo> orderItemInfos = orderItems.stream()
            .map(OrderItemInfo::new)
            .toList();

        return OrderCreateResponseDto.builder()
            .storeId(store.getStoreId())
            .userId(user.getId())
            .orderId(order.getId())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .items(orderItemInfos)
            .build();
    }

    /**
     * 주문 요청 수락
     *
     * @param user
     * @param orderId
     */
    public void acceptOrder(User user, Long orderId) {
        // todo: store 사장이랑 user가 같은지 확인
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        // 중복 요청 체크
        if (order.getStatus() == OrderStatus.ACCEPTED) {
            // todo: 이미 수락한 요청입니다.
        }

        // 주문 수락되면 장바구니 목록 삭제
        cartItemRepository.deleteAllByCart_User(user);

        order.updateStatus(OrderStatus.ACCEPTED);
    }

    /**
     * 배달 출발
     * @param user 가게 사장
     * @param orderId
     */
    public void startDelivery(User user, Long orderId) {
        // todo: store 사장이랑 user가 같은지 확인
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        // 중복 요청 체크
        if (order.getStatus() == OrderStatus.DELIEVERY) {
            // todo: 이미 배달 중 입니다.
        }

        order.updateStatus(OrderStatus.DELIEVERY);
    }

    /**
     * 주문 완료
     * @param user 가게 사장
     * @param orderId
     */
    public void completeOrder(User user, Long orderId) {
        // todo: store 사장이랑 user가 같은지 확인

        Order order = orderRepository.findByIdOrElseThrow(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            // todo: 이미 완료된 주문입니다.
        }

        order.updateStatus(OrderStatus.COMPLETED);
    }

    /**
     * 현재 주문 상태
     * @param orderId
     * @return
     */
    public OrderStatusResponseDto getCurrentOrderStatus(User user, Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        if(/*todo: 가게 사장인지 확인*/ !order.getUser().equals(user)) {
            throw new IllegalArgumentException("가게 사장 혹은 주문자가 아닙니다.");
        }

        return new OrderStatusResponseDto(order.getStatus());
    }
}
