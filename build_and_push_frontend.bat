@echo off

:: Build and push frontend
docker-compose -f docker-compose.frontend.build-and-push.yml build
docker-compose -f docker-compose.frontend.build-and-push.yml push

echo Frontend build and push completed!
pause
