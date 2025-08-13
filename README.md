# BulkMagic Notification Service

A Spring Boot-based notification service that integrates with Slack webhooks to send order notifications in real-time.

## Features

- **Slack Integration**: Send notifications directly to Slack channels using webhooks
- **Order Processing**: Parse and format order data from eCommerce systems
- **Template-based Messages**: Use customizable notification templates
- **Real-time Notifications**: Instant delivery of order updates
- **Configurable**: Easy configuration through environment variables or application properties

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Slack workspace with webhook access
- Spring Boot 3.2.0

## Setup

### 1. Slack Webhook Configuration

1. Go to your Slack workspace
2. Navigate to **Apps** → **Custom Integrations** → **Incoming Webhooks**
3. Click **Add Configuration**
4. Choose the channel where you want to receive notifications
5. Copy the webhook URL

### 2. Environment Variables

Set the following environment variables:

```bash
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK"
export SLACK_CHANNEL="#orders"
export SLACK_USERNAME="BulkMagic Bot"
export SLACK_ICON_EMOJI=":shopping_cart:"
```

### 3. Application Configuration

Update `src/main/resources/application.yml` with your Slack webhook URL:

```yaml
slack:
  webhook:
    url: https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
  channel: #orders
  username: BulkMagic Bot
  icon-emoji: :shopping_cart:
```

## API Endpoints

### Send Order Notification

**POST** `/api/slack/order-notification`

Sends an order notification to Slack based on the provided payload.

**Request Body:**
```json
{
  "__typename": "OrderCreated",
  "order": {
    "id": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4",
    "created": "2025-07-24T02:12:05.764991+00:00",
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
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Slack notification sent successfully",
  "orderId": "T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4"
}
```

### Test Notification

**POST** `/api/slack/test`

Sends a test notification to verify Slack integration.

**Response:**
```json
{
  "status": "success",
  "message": "Test notification sent successfully"
}
```

### Health Check

**GET** `/api/slack/health`

Checks the service health status.

**Response:**
```json
{
  "status": "healthy",
  "service": "Slack Notification Service",
  "timestamp": "2025-01-27T10:30:00"
}
```

## Notification Template

The service automatically formats order notifications using this template:

```
You've received a new order on BulkMagic.

Items Ordered
Item                           Quantity   Price
----                           --------   -----
Apple Juice                    1          $1.99

Order Details:
✅    Order ID: `T3JkZXI6NjE4NmMyYTAtMjM3MS00ZGRkLWI0YmEtMzQ0OWE3MjZmYjI4`
✅    Placed At: Jan 27, 2025 at 02:12
✅    Buyer: Austin

Please prepare this order for pickup by 02:22.

Thank you,
BulkMagic Team
```

## Running the Service

### 1. Build the Project

```bash
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or using the JAR file:

```bash
java -jar target/notification-0.0.1-SNAPSHOT.jar
```

### 3. Test the Integration

```bash
# Test notification
curl -X POST http://localhost:8080/api/slack/test

# Health check
curl http://localhost:8080/api/slack/health
```

## Docker Support

The service includes Docker support for easy deployment:

```bash
# Build Docker image
docker build -t bulkmagic-notification .

# Run container
docker run -p 8080:8080 \
  -e SLACK_WEBHOOK_URL="your-webhook-url" \
  -e SLACK_CHANNEL="#orders" \
  bulkmagic-notification
```

## Error Handling

The service includes comprehensive error handling:

- **Webhook Failures**: Logs and reports webhook delivery failures
- **Invalid Payloads**: Validates JSON structure and provides clear error messages
- **Network Issues**: Handles timeouts and connection problems gracefully
- **Slack API Errors**: Reports Slack-specific error responses

## Logging

The service uses SLF4J with Spring Boot's default logging configuration. Logs include:

- Order processing details
- Slack webhook responses
- Error conditions and stack traces
- Service health information

## Security Considerations

- **Webhook URLs**: Keep webhook URLs secure and private
- **Environment Variables**: Use environment variables for sensitive configuration
- **Input Validation**: All incoming payloads are validated before processing
- **Rate Limiting**: Consider implementing rate limiting for production use

## Troubleshooting

### Common Issues

1. **Webhook URL Invalid**: Verify the webhook URL is correct and active
2. **Channel Not Found**: Ensure the specified Slack channel exists
3. **Permission Issues**: Check that the webhook has permission to post to the channel
4. **Network Connectivity**: Verify the service can reach Slack's servers

### Debug Mode

Enable debug logging by adding to `application.yml`:

```yaml
logging:
  level:
    com.ecommerce.notification: DEBUG
    org.springframework.web.client: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License. 