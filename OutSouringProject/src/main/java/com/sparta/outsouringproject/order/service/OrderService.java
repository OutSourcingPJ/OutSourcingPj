package com.sparta.outsouringproject.order.service;

import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
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
     * 주문 요청 수락
     *
     * @param user
     * @param storeId
     * @param orderId
     */
    void acceptOrder(User user, Long storeId, Long orderId);

    /**
     * 배달 출발
     *
     * @param user    가게 사장
     * @param storeId
     * @param orderId
     */
    void startDelivery(User user, Long storeId, Long orderId);

    /**
     * 주문 완료
     *
     * @param user    가게 사장
     * @param storeId
     * @param orderId
     */
    void completeOrder(User user, Long storeId, Long orderId);

    /**
     * 현재 주문 상태
     * @param orderId
     * @return
     */
    OrderStatusResponseDto getCurrentOrderStatus(User user, Long orderId);
}
