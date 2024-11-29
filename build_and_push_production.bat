@echo off

:: Build and push production
docker-compose -f docker-compose.production.build-and-push.yml build
docker-compose -f docker-compose.production.build-and-push.yml push

echo Production build and push completed!
pause
