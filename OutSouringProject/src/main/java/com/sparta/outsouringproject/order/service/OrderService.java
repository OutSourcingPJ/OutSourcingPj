package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
//import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.user.entity.User;

public interface OrderService {
    /**
     * 주문 생성
     *
     * @param orderRequest
     */
    OrderCreateResponseDto createOrder(User user, OrderCreateRequestDto orderRequest);

    /**
     * 주문 상태 변경
     *
     * @param user
     * @param storeId
     * @param orderId
     * @param requestDto
     */
//    void changeOrderStatus(User user, Long storeId, Long orderId, OrderStatusChangeRequestDto requestDto);

    /**
     * 현재 주문 상태
     * @param orderId
     * @return
     */
    OrderStatusResponseDto getCurrentOrderStatus(User user, Long orderId);
}
