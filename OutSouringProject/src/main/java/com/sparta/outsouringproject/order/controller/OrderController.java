package com.sparta.outsouringproject.order.controller;

import com.sparta.outsouringproject.order.dto.OrderCreateRequestDto;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 요청
     */
    @PostMapping()
    public  ResponseEntity<OrderCreateResponseDto> requestOrder(@RequestBody OrderCreateRequestDto orderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(null, orderRequest));
    }

    /**
     * 주문 요청 수락
     */
    @PatchMapping("/{orderId}/accept")
    public  ResponseEntity<Void> acceptOrder(@PathVariable("orderId") Long orderId){
        orderService.acceptOrder(null, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 주문 배달 출발
     */
    @PatchMapping("/{orderId}/delivery")
    public  ResponseEntity<Void> startDelivery(@PathVariable("orderId") Long orderId) {
        orderService.startDelivery(null, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 배달완료
     */
    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeDelivery(@PathVariable("orderId") Long orderId) {
        orderService.completeOrder(null, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 주문 상태
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderStatusResponseDto> currentOrderStatus(@PathVariable("orderId") Long orderId) {
        return new ResponseEntity<>(orderService.getCurrentOrderStatus(null, orderId), HttpStatus.OK);
    }
}
