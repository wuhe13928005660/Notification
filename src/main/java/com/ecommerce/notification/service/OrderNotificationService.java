package com.ecommerce.notification.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Log4j2
@Service
public class OrderNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    public String sendOrderNotification(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        JsonNode order = root.path("order");

        String orderId = order.path("id").asText();
        String created = order.path("created").asText();
        String buyer = order.path("user").path("firstName").asText();



        // Build items table with proper formatting
        StringBuilder itemsTable = new StringBuilder();
        itemsTable.append("Items Ordered\n");
        itemsTable.append("Item                           Quantity   Price     \n");
        itemsTable.append("----                           --------   -----     \n");
        
        for (JsonNode line : order.path("lines")) {
            int quantity = line.path("quantity").asInt();
            String productName = line.path("variant").path("product").path("name").asText();
            double price = line.path("variant").path("product").path("price").asDouble(0.0); // Default to 0 if price not available
            itemsTable.append(String.format("%-30s %-10d $%-8.2f\n", productName, quantity, price));
        }
        
        // Format the timestamp to be more readable
        String formattedTimestamp = created.replace("T", " ").replace("+00:00", "");
        
        // Calculate pickup time (10 minutes from order time)
        String pickupTime = "20:40"; // You can make this dynamic based on order time
        
        String emailText = String.format(
            "You've received a new order on BulkMagic.\n\n" +
            "%s\n\n" +
            "Order Details:\n" +
            "✓ Order ID: %s\n" +
            "✓ Placed At: %s\n" +
            "✓ Buyer: %s\n\n" +
            "Please prepare this order for pickup by %s.\n\n" +
            "Thank you,\n" +
            "BulkMagic Team",
            itemsTable.toString(),
            orderId,
            formattedTimestamp,
            buyer,
            pickupTime
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("william13928005660@gmail.com");
        message.setSubject("New Order Notification");
        message.setText(emailText);

        mailSender.send(message);
        return orderId;
    }
} 