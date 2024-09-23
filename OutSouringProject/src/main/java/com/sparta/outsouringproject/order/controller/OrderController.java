package com.sparta.outsouringproject.order.controller;

import com.sparta.outsouringproject.common.annotation.Auth;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.dto.ResponseDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 요청
     */
    @PostMapping("/orders")
    public ResponseEntity<ResponseDto<OrderCreateResponseDto>> requestOrder(
        @Auth AuthUser authUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto.of(HttpStatus.CREATED, orderService.createOrder(authUser)));
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/stores/{storeId}/orders/{orderId}/status")
    public ResponseEntity<ResponseDto<Void>> acceptOrder(@Auth AuthUser authUser,
        @PathVariable("storeId") Long storeId,
        @PathVariable("orderId") Long orderId,
        @RequestBody OrderStatusChangeRequestDto requestDto) {
        orderService.changeOrderStatus(authUser, storeId, orderId, requestDto);
        return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "정상적으로 변경되었습니다."));
    }


    /**
     * 현재 주문 상태
     */
    @GetMapping("orders/{orderId}")
    public ResponseEntity<ResponseDto<OrderStatusResponseDto>> currentOrderStatus(
        @Auth AuthUser authUser,
        @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(
            ResponseDto.of(HttpStatus.OK, orderService.getCurrentOrderStatus(authUser, orderId)));
    }

    /**
     * 현재 가게의 주문요청 리스트
     */
    @GetMapping("/stores/{storeId}/orders")
    public ResponseEntity<ResponseDto<List<OrderItemInfo>>> getAllOrders(
        @Auth AuthUser authUser, @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(
            ResponseDto.of(HttpStatus.OK, orderService.getAllOrdersByStoreId(authUser, storeId)));
    }
}
