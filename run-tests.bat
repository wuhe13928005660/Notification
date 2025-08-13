@echo off
echo 🧪 Running Slack Notification Service Unit Tests
echo ================================================

echo.
echo Building and running tests...
mvn test -Dtest=SlackNotificationServiceTest

echo.
echo ✅ Tests completed!
echo.
echo Test Results Summary:
echo - Multiple products handling
echo - Single product handling  
echo - Large quantities handling
echo - Empty lines handling
echo - High precision prices
echo - Missing amount handling
echo - Invalid JSON handling
echo - Missing order handling
echo - Test notification
echo - Webhook failure handling
echo.
pause 