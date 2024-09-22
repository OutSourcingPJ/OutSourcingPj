package com.sparta.outsouringproject.common.aop;

import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class OrderLogAop {

    @AfterReturning(value= "execution(* com.sparta.outsouringproject.order.service.OrderServiceImpl.createOrder(..))", returning = "response")
    public void logOrderCreation(JoinPoint joinPoint, Object response) {
        OrderCreateResponseDto orderCreateResponseDto = (OrderCreateResponseDto) response;
        Long storeId = orderCreateResponseDto.getStoreId();
        Long orderId = orderCreateResponseDto.getOrderId();
        LocalDateTime cur = LocalDateTime.now();
        log.info("::: 신규 주문 ::: 가게 ID: {} / 주문 ID: {} / 요청시각: {}", storeId, orderId, cur);
    }

    @AfterReturning("execution(* com.sparta.outsouringproject.order.service.OrderServiceImpl.changeOrderStatus(..))")
    public void logOrderStatusChange(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long storeId = (Long) args[1]; // todo: @AuthUser 추가되면 1로 바꿔야함
        Long orderId = (Long) args[2]; // todo: @AuthUser 추가되면 2로 바꿔야함
        LocalDateTime cur = LocalDateTime.now();
        log.info("::: 배달상태 변경 ::: 가게 ID: {} / 주문 ID: {} / 요청시각: {}", storeId, orderId, cur);
    }
}
