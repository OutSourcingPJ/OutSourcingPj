package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import java.util.List;

public interface OrderService {

    /**
     * 주문 생성
     *
     * @param auth
     */
    OrderCreateResponseDto createOrder(AuthUser auth);

    /**
     * 주문 상태 변경
     *
     * @param auth
     * @param storeId
     * @param orderId
     * @param requestDto
     */
    void changeOrderStatus(AuthUser auth, Long storeId, Long orderId,
        OrderStatusChangeRequestDto requestDto);

    /**
     * 현재 주문 상태
     *
     * @param auth
     * @param orderId
     * @return
     */
    OrderStatusResponseDto getCurrentOrderStatus(AuthUser auth, Long orderId);

    List<OrderItemInfo> getAllOrdersByStoreId(AuthUser authUser, Long storeId);
}
