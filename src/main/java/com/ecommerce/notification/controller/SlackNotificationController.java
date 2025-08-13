package com.ecommerce.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.notification.service.SlackNotificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/slack")
public class SlackNotificationController {

    @Autowired
    private SlackNotificationService slackNotificationService;

    @PostMapping("/order-notification")
    public ResponseEntity<Map<String, String>> sendOrderNotification(@RequestBody String payload) {
        try {
            String orderId = slackNotificationService.sendOrderNotification(payload);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Slack notification sent successfully",
                "orderId", orderId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Failed to send Slack notification: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification() {
        try {
            slackNotificationService.sendTestNotification();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Test notification sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Failed to send test notification: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "Slack Notification Service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
} 