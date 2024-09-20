package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.notification.service.OrderStatusTracker;
import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.order.repository.OrderItemRepository;
import com.sparta.outsouringproject.order.repository.OrderRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderStatusTracker orderStatusTracker;

    @Override
    public OrderCreateResponseDto createOrder(User user, OrderCreateRequestDto orderRequest) {
        if (orderRequest.getCarts().isEmpty()) {
            throw new IllegalArgumentException("장바구니에 상품이 존재하지 않습니다.");
        }

        Store store = storeRepository.findById(orderRequest.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        LocalTime now = LocalDateTime.now().toLocalTime();
        if(now.isBefore(store.getOpenTime()) && now.isAfter(store.getCloseTime())) {
            throw new IllegalArgumentException("주문 가능 시간이 아닙니다.");
        }

        int totalPrice = 0;
        for(CartItemInfo cartItemInfo : orderRequest.getCarts()) {
            totalPrice += cartItemInfo.getPrice();
        }

        if(store.getOrderAmount() < totalPrice){
            throw new IllegalArgumentException("주문 금액이 최소 주문 금액보다 낮습니다.");
        }

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

    @Override
    public void changeOrderStatus(User user, Long storeId, Long orderId,
        OrderStatusChangeRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found"));

//        if(store.getUser() == null || !store.getUser().equals(user)){
//            throw new IllegalArgumentException("가게의 사장 정보가 없거나, 가게의 사장님이 아닙니다.");
//        }

        Order order = orderRepository.findByIdOrElseThrow(orderId);

        order.updateStatus(requestDto.getOrderStatus());
        orderStatusTracker.onOrderStatusChanged(order.getId(), order.getStatus());

        // 주문 수락되면 장바구니 목록 삭제
        if(order.getStatus().equals(OrderStatus.ACCEPTED)){
            cartItemRepository.deleteAllByCart_User(user);
        }

        // 주문이 완료되면 기록 저장
        if(order.getStatus().equals(OrderStatus.COMPLETED)) {
            List<OrderItem> items = orderItemRepository.findAllByOrder(order);
            List<OrderHistory> historyList = new ArrayList<>();

            for(OrderItem item : items){
                OrderHistory history = OrderHistory.builder()
                    .soldDate(LocalDateTime.now())
                    .orderId(item.getOrder().getId())
                    .storeId(item.getOrder().getStore().getStoreId())
                    .userId(item.getOrder().getUser().getId())
                    .menuId(item.getMenu().getMenu_id())
                    .menuName(item.getMenu().getMenuName())
                    .quantity(item.getQuantity())
                    .soldPrice(item.getPrice())
                    .soldTotalPrice(item.getTotalPrice())
                    .build();

                historyList.add(history);
            }

            if(!historyList.isEmpty()){
                orderHistoryRepository.saveAll(historyList);
            }

            orderRepository.delete(order);
        }
    }



    @Override
    public OrderStatusResponseDto getCurrentOrderStatus(User user, Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        if(!user.equals(order.getStore().getUser()) && !user.equals(order.getUser())) {
            throw new IllegalArgumentException("가게 사장 혹은 주문자가 아닙니다.");
        }

        return new OrderStatusResponseDto(order.getStatus());
    }
}
