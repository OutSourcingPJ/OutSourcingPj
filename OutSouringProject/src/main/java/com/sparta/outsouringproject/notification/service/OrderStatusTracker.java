package com.sparta.outsouringproject.notification.service;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.repository.OrderRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStatusTracker {

    private final Map<Long, SseEmitter> emitters = new HashMap<>();
    private final OrderRepository orderRepository;

    public SseEmitter createEmitter(Long orderId) throws IOException {
        Order order = orderRepository.findById(orderId)
            .orElse(null);
        SseEmitter emitter = new SseEmitter(0L);

        if (order == null) {
            emitter.send(SseEmitter.event()
                .name("error")
                .data("주문을 찾을 수 없습니다.")
                .reconnectTime(3000));
            emitter.completeWithError(new IllegalArgumentException("Order not found"));
            return emitter;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            emitter.send(SseEmitter.event()
                .name("error")
                .data("이미 완료된 주문입니다.")
                .reconnectTime(3000));
            emitter.completeWithError(new IllegalArgumentException("Order already completed"));
            return emitter;
        }

        if (emitters.containsKey(orderId)) {
            return emitters.get(orderId);
        }

        setEmitterConfig(orderId, emitter);
        emitters.put(orderId, emitter);
        return emitter;
    }

    public void onOrderStatusChanged(Long orderId, OrderStatus status) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        if (emitters.containsKey(orderId)) {
            SseEmitter emitter = emitters.get(orderId);
            try {
                emitter.send(SseEmitter.event()
                    .data(status.name()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setEmitterConfig(Long key, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            emitters.remove(key);
        });

        emitter.onTimeout(() -> {
            emitters.remove(key);
        });

        emitter.onError((err) -> {
            log.info(err.getMessage());
            emitters.remove(key);
        });
    }
}
