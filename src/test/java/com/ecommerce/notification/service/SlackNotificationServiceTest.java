package com.ecommerce.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class SlackNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SlackNotificationService slackNotificationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Set real ObjectMapper instead of mocked one
        ReflectionTestUtils.setField(slackNotificationService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(slackNotificationService, "slackWebhookUrl", "https://hooks.slack.com/test");
        ReflectionTestUtils.setField(slackNotificationService, "slackChannel", "#test");
        ReflectionTestUtils.setField(slackNotificationService, "slackUsername", "Test Bot");
        ReflectionTestUtils.setField(slackNotificationService, "slackIconEmoji", ":test:");
        
        // Mock successful webhook response by default
        lenient().when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn("ok");
    }

    @Test
    void testSendOrderNotificationWithMultipleProducts() throws Exception {
        // Arrange
        String payload = createMultipleProductsPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
        
        // Verify webhook was called
        verify(restTemplate, times(1)).postForObject(
            eq("https://hooks.slack.com/test"), 
            any(), 
            eq(String.class)
        );
    }

    @Test
    void testSendOrderNotificationWithSingleProduct() throws Exception {
        // Arrange
        String payload = createSingleProductPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
    }

    @Test
    void testSendOrderNotificationWithLargeQuantities() throws Exception {
        // Arrange
        String payload = createLargeQuantitiesPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
    }

    @Test
    void testSendOrderNotificationWithEmptyLines() throws Exception {
        // Arrange
        String payload = createEmptyLinesPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
    }

    @Test
    void testSendOrderNotificationWithHighPrecisionPrices() throws Exception {
        // Arrange
        String payload = createHighPrecisionPricesPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
    }

    @Test
    void testSendOrderNotificationWithMissingAmount() throws Exception {
        // Arrange
        String payload = createMissingAmountPayload();
        
        // Act
        String result = slackNotificationService.sendOrderNotification(payload);
        
        // Assert
        assertNotNull(result);
        assertEquals("T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4", result);
    }

    @Test
    void testSendOrderNotificationWithInvalidJson() {
        // Arrange
        String invalidPayload = "{ invalid json }";
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            slackNotificationService.sendOrderNotification(invalidPayload);
        });
    }

    @Test
    void testSendOrderNotificationWithMissingOrder() {
        // Arrange
        String payload = "{\"__typename\": \"OrderCreated\"}";
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            slackNotificationService.sendOrderNotification(payload);
        });
    }

    @Test
    void testSendTestNotification() throws Exception {
        // Act
        slackNotificationService.sendTestNotification();
        
        // Assert
        verify(restTemplate, times(1)).postForObject(
            eq("https://hooks.slack.com/test"), 
            any(), 
            eq(String.class)
        );
    }

    @Test
    void testWebhookFailure() {
        // Arrange
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("Webhook failed"));
        
        String payload = createSingleProductPayload();
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            slackNotificationService.sendOrderNotification(payload);
        });
    }

    // Helper methods to create test payloads

    private String createMultipleProductsPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "amount": 25.97,
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 2,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  },
                  {
                    "quantity": 1,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "product": {
                        "id": "UHJvZHVjdDoxNTM=",
                        "name": "Orange Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  },
                  {
                    "quantity": 3,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg2",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg2",
                      "product": {
                        "id": "UHJvZHVjdDoxNTQ=",
                        "name": "Grape Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }

    private String createSingleProductPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "amount": 1.99,
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 1,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }

    private String createLargeQuantitiesPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "amount": 199.50,
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 50,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  },
                  {
                    "quantity": 25,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "product": {
                        "id": "UHJvZHVjdDoxNTM=",
                        "name": "Orange Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }

    private String createEmptyLinesPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "amount": 5.97,
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 1,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  },
                  {
                    "quantity": 0,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg1",
                      "product": {
                        "id": "UHJvZHVjdDoxNTM=",
                        "name": "Orange Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }

    private String createHighPrecisionPricesPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "amount": 3.333333,
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 1,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }

    private String createMissingAmountPayload() {
        return """
            {
              "__typename": "OrderCreated",
              "order": {
                "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
                "created": "2025-08-11T20:30:00.000000+00:00",
                "paymentStatus": "NOT_CHARGED",
                "total": {
                  "gross": {
                    "currency": "USD"
                  }
                },
                "lines": [
                  {
                    "quantity": 1,
                    "variant": {
                      "id": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "name": "UHJvZHVjdFZhcmlhbnQ6Mzg0",
                      "product": {
                        "id": "UHJvZHVjdDoxNTI=",
                        "name": "Apple Juice",
                        "metadata": [
                          {
                            "key": "vendor_id",
                            "value": "2"
                          }
                        ]
                      }
                    }
                  }
                ],
                "user": {
                  "id": "VXNlcjoxMg==",
                  "email": "test@example.com",
                  "firstName": "TestUser"
                }
              }
            }
            """;
    }
} 