#!/bin/bash

# Test script for Slack Notification Service
# Make sure the service is running on localhost:8080

echo "🚀 Testing BulkMagic Slack Notification Service"
echo "=============================================="

# Test health endpoint
echo -e "\n1. Testing health check..."
curl -s http://localhost:8080/api/slack/health | jq '.'

# Test notification endpoint
echo -e "\n2. Testing order notification..."
curl -s -X POST http://localhost:8080/api/slack/order-notification \
  -H "Content-Type: application/json" \
  -d '{
    "__typename": "OrderCreated",
    "order": {
      "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
      "created": "2025-01-27T02:12:05.764991+00:00",
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
        "email": "austin.sparks@example.com",
        "firstName": "Austin"
      }
    }
  }' | jq '.'

# Test simple notification
echo -e "\n3. Testing simple test notification..."
curl -s -X POST http://localhost:8080/api/slack/test | jq '.'

echo -e "\n✅ Testing completed!"
echo -e "\nNote: Make sure you have:"
echo "1. Set your SLACK_WEBHOOK_URL environment variable"
echo "2. The service is running on localhost:8080"
echo "3. jq is installed for JSON formatting (optional)" 