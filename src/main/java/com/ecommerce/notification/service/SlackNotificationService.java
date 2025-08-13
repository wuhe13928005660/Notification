package com.ecommerce.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class SlackNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Value("${slack.channel:general}")
    private String slackChannel;

    @Value("${slack.username:BulkMagic Bot}")
    private String slackUsername;

    @Value("${slack.icon-emoji::shopping_cart:}")
    private String slackIconEmoji;

    public String sendOrderNotification(String payload) throws Exception {
        log.info("Processing order notification for Slack");
        
        JsonNode root = objectMapper.readTree(payload);
        JsonNode order = root.path("order");

        // Extract order details
        String orderId = order.path("id").asText();
        String created = order.path("created").asText();
        
        // Fix: Extract amount with proper error handling
        double amount = 0.0;
        try {
            JsonNode totalNode = order.path("total").path("gross").path("amount");
            log.info("Total node: {}", totalNode);
            if (!totalNode.isMissingNode() && !totalNode.isNull()) {
                amount = totalNode.asDouble();
                log.info("Extracted amount: {}", amount);
            } else {
                log.warn("Amount node is missing or null");
            }
        } catch (Exception e) {
            log.warn("Could not extract amount from order, using default: {}", e.getMessage());
            amount = 0.0;
        }
        
        String currency = order.path("total").path("gross").path("currency").asText();
        String buyerFirstName = order.path("user").path("firstName").asText();
        String buyerEmail = order.path("user").path("email").asText();

        // Build ordered items list
        StringBuilder orderedItems = new StringBuilder();
        StringBuilder quantityList = new StringBuilder();
        StringBuilder priceList = new StringBuilder();
        
        for (JsonNode line : order.path("lines")) {
            int quantity = line.path("quantity").asInt();
            String productName = line.path("variant").path("product").path("name").asText();
            
            // Use the total amount as the price since individual line prices aren't available
            double linePrice = amount;
            
            orderedItems.append("• ").append(productName).append("\n");
            quantityList.append(quantity).append("\n");
            priceList.append("$").append(String.format("%.2f", linePrice)).append("\n");
        }

        // Format timestamp
        LocalDateTime orderTime = LocalDateTime.parse(created.substring(0, 19));
        String formattedTime = orderTime.format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.ENGLISH)
        );
        
        // Calculate pickup time (10 minutes from order time)
        LocalDateTime pickupTime = orderTime.plusMinutes(10);
        String formattedPickupTime = pickupTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Build Slack message using the template
        String slackMessage = buildSlackMessage(
            orderedItems.toString(),
            quantityList.toString(),
            priceList.toString(),
            orderId,
            formattedTime,
            buyerFirstName,
            formattedPickupTime
        );

        // Send to Slack
        sendSlackNotification(slackMessage);
        
        log.info("Slack notification sent successfully for order: {}", orderId);
        return orderId;
    }

    private String buildSlackMessage(String orderedItems, String quantities, String prices, 
                                   String orderId, String timestamp, String buyerName, String pickupTime) {
        return String.format(
            "You've received a new order on BulkMagic.\n\n" +
            "*Items Ordered*\n" +
            "```\n" +
            "%-30s %-10s %-10s\n" +
            "%-30s %-10s %-10s\n" +
            "%s" +
            "```\n\n" +
            "*Order Details:*\n" +
            ":heavy_check_mark:    Order ID: `%s`\n" +
            ":heavy_check_mark:    Placed At: %s\n" +
            ":heavy_check_mark:    Buyer: %s\n\n" +
            "Please prepare this order for pickup by *%s*.\n\n" +
            "Thank you,\n" +
            "BulkMagic Team",
            "Item", "Quantity", "Price",
            "----", "--------", "-----",
            formatOrderTable(orderedItems, quantities, prices),
            orderId,
            timestamp,
            buyerName,
            pickupTime
        );
    }

    private String formatOrderTable(String items, String quantities, String prices) {
        String[] itemLines = items.split("\n");
        String[] quantityLines = quantities.split("\n");
        String[] priceLines = prices.split("\n");
        
        StringBuilder table = new StringBuilder();
        for (int i = 0; i < itemLines.length; i++) {
            if (itemLines[i].trim().isEmpty()) continue;
            
            String item = itemLines[i].replace("• ", "").trim();
            String quantity = i < quantityLines.length ? quantityLines[i].trim() : "";
            String price = i < priceLines.length ? priceLines[i].trim() : "";
            
            // Ensure proper alignment with fixed widths
            String formattedItem = String.format("%-30s", item);
            String formattedQuantity = String.format("%-10s", quantity);
            String formattedPrice = String.format("%-10s", price);
            
            table.append(formattedItem).append(" ").append(formattedQuantity).append(" ").append(formattedPrice).append("\n");
        }
        return table.toString();
    }

    private void sendSlackNotification(String message) {
        try {
            ObjectNode slackPayload = objectMapper.createObjectNode();
            slackPayload.put("channel", slackChannel);
            slackPayload.put("username", slackUsername);
            slackPayload.put("icon_emoji", slackIconEmoji);
            slackPayload.put("text", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(slackPayload.toString(), headers);
            
            String response = restTemplate.postForObject(slackWebhookUrl, request, String.class);
            log.info("Slack webhook response: {}", response);
            
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }

    public void sendTestNotification() {
        String testMessage = "🚀 *BulkMagic Notification Service Test*\n\n" +
                           "This is a test message to verify Slack integration is working properly.\n" +
                           "Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        sendSlackNotification(testMessage);
    }
} 