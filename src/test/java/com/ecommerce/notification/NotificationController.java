package com.ecommerce.notification.controller;

import com.ecommerce.notification.service.OrderNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    @Autowired
    private OrderNotificationService notificationService;

    @PostMapping("/order")
    public void notifyOrder(@RequestBody String payload) throws Exception {
        notificationService.sendOrderNotification(payload);
    }
}
