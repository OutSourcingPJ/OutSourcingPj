package com.sparta.outsouringproject.order.controller;

import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.service.OrderService;
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
    public  ResponseEntity<OrderCreateResponseDto> requestOrder(@RequestBody OrderCreateRequestDto orderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(null, orderRequest));
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/stores/{storeId}/orders/{orderId}/status")
    public  ResponseEntity<Void> acceptOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, OrderStatusChangeRequestDto requestDto){
        orderService.changeOrderStatus(null, storeId, orderId, requestDto);
        return ResponseEntity.ok().build();
    }


    /**
     * 현재 주문 상태
     */
    @GetMapping("orders/{orderId}")
    public ResponseEntity<OrderStatusResponseDto> currentOrderStatus(@PathVariable("orderId") Long orderId) {
        return new ResponseEntity<>(orderService.getCurrentOrderStatus(null, orderId), HttpStatus.OK);
    }
}
