package com.sparta.outsouringproject.notification.service;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import java.io.IOException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface OrderStatusTracker {

    SseEmitter createEmitter(Long orderId) throws IOException;
    void onOrderStatusChanged(Long orderId, OrderStatus status);
}
