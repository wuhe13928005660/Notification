package com.ecommerce.notification.controller;

import com.ecommerce.notification.service.OrderNotificationService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Log4j2
@RestController
@RequestMapping("/webhook")
public class NotificationController {

    @Autowired
    private OrderNotificationService notificationService;

    @PostMapping("/order")
    public ResponseEntity<String> receiveOrderWebhook(@RequestBody String payload) throws Exception {
       
        String orderId = notificationService.sendOrderNotification(payload);
        HttpStatus status = HttpStatus.OK;
        // log.info("Order_id:"+orderId);
        // log.info("Order_id:"+orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
} 