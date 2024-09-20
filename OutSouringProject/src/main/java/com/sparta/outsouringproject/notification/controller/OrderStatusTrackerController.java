package com.sparta.outsouringproject.notification.controller;

import com.sparta.outsouringproject.notification.service.OrderStatusTracker;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class OrderStatusTrackerController {

    private final OrderStatusTracker orderStatusTracker;

    @GetMapping("/tracker")
    public String tracker() {
        return "tracker";
    }

    @GetMapping(value = "/connect/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter connect(@PathVariable("orderId") Long orderId) {
        try {
            return orderStatusTracker.createEmitter(orderId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
