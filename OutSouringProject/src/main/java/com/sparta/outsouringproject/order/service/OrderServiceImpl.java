package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.common.exceptions.AccessDeniedException;
import com.sparta.outsouringproject.common.exceptions.AuthException;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.notification.service.OrderStatusTracker;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.order.repository.OrderItemRepository;
import com.sparta.outsouringproject.order.repository.OrderRepository;
import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderStatusTracker orderStatusTracker;

    @Override
    public OrderCreateResponseDto createOrder(AuthUser auth) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());
        List<CartItem> cartItems = cartItemRepository.findAllByCart_User(user);
        if (cartItems.isEmpty()) {
            throw new InvalidRequestException("장바구니에 상품이 존재하지 않습니다.");
        }

        Store store = storeRepository.findById(cartItems.get(0).getMenu().getStore().getStoreId())
            .orElseThrow(() -> new InvalidRequestException("존재하지 않는 가게입니다."));

        LocalTime now = LocalDateTime.now().toLocalTime();
        if(now.isBefore(store.getOpenTime()) && now.isAfter(store.getCloseTime())) {
            throw new InvalidRequestException("주문 가능 시간이 아닙니다.");
        }

        int totalPrice = 0;

        long userCartId = user.getCart().getId();

        for(CartItem cartItem : cartItems) {
            if(cartItem.getCart().getId() != userCartId) {
                throw new AccessDeniedException("요청한 장바구니는 현재 유저의 장바구니가 아닙니다.");
            }
            totalPrice += cartItem.getPrice();
        }

        if(totalPrice < 0) {
            throw new InvalidRequestException("총 금액은 0보다 커야합니다.");
        }

        if(store.getOrderAmount() < totalPrice){
            throw new InvalidRequestException("주문 금액이 최소 주문 금액보다 낮습니다.");
        }

        Order order = new Order(user, store, OrderStatus.WAITING);
        order = orderRepository.save(order);

        // 주문 생성 후 주문 상세 테이블 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItemInfo : cartItems) {
            Long menuId = cartItemInfo.getMenu().getMenu_id();
            Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            OrderItem orderItem = new OrderItem(order, menu, cartItemInfo.getQuantity(),
                cartItemInfo.getPrice(), cartItemInfo.getTotalPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        // 장바구니 목록 초기화
        cartItemRepository.deleteAllByCart_User(user);

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
    public void changeOrderStatus(AuthUser auth, Long storeId, Long orderId,
        OrderStatusChangeRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new InvalidRequestException("존재하지 않는 가게입니다."));

        User user = userRepository.findByIdOrElseThrow(auth.getId());

        if(store.getUser() == null){
            throw new InvalidRequestException("가게의 사장 정보가 없습니다.");
        }

        if(!user.equals(store.getUser())){
            throw new AccessDeniedException("가게의 주인이 아닙니다.");
        }

        Order order = orderRepository.findByIdOrElseThrow(orderId);

        order.updateStatus(requestDto.getOrderStatus());
        orderStatusTracker.onOrderStatusChanged(order.getId(), order.getStatus());


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
            orderItemRepository.deleteAll(items);
            orderRepository.delete(order);
        }
    }



    @Override
    public OrderStatusResponseDto getCurrentOrderStatus(AuthUser auth, Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        if(auth.getRole() == Role.OWNER && !user.equals(order.getStore().getUser())) {
            throw new AccessDeniedException("해당 가게의 사장이 아닙니다.");
        }

        if(auth.getRole() == Role.USER && !user.equals(order.getUser())) {
            throw new AccessDeniedException("주문자가 아닙니다.");
        }

        return new OrderStatusResponseDto(order.getStatus());
    }

    @Override
    public List<OrderItemInfo> getAllOrdersByStoreId(AuthUser auth, Long storeId) {

        Store store = storeRepository.findStoreWithOrdersById(storeId)
            .orElseThrow(() -> new InvalidRequestException("존재하지 않는 가게입니다."));

        User user = userRepository.findByIdOrElseThrow(auth.getId());
        if(auth.getRole() == Role.OWNER && store.getUser() != user) {
            throw new AccessDeniedException("해당 가게의 사장이 아닙니다.");
        }

        List<OrderItem> allByOrderIn = orderItemRepository.findAllByOrderIn(store.getOrders());
        return allByOrderIn.stream().map(OrderItemInfo::new).toList();
    }
}
