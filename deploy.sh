#!/bin/bash

echo "🚀 Starting deployment with Docker Compose..."

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Build JAR
echo -e "${YELLOW} Building JAR file...${NC}"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED} Maven build failed!${NC}"
    exit 1
fi

echo -e "${GREEN} JAR built successfully${NC}"

# Step 2: Stop old containers
echo -e "${YELLOW} Stopping old containers...${NC}"
docker-compose down

# Step 3: Build and start containers
echo -e "${YELLOW} Building Docker images and starting containers...${NC}"
docker-compose up -d --build

if [ $? -ne 0 ]; then
    echo -e "${RED} Docker Compose failed!${NC}"
    exit 1
fi

# Step 4: Wait for services to be healthy
echo -e "${YELLOW} Waiting for services to be healthy...${NC}"
sleep 10

# Step 5: Check container status
echo -e "${YELLOW} Container status:${NC}"
docker-compose ps

# Step 6: Show logs
echo -e "${YELLOW} Recent logs:${NC}"
docker-compose logs --tail=50

# Step 7: Test health endpoint
echo -e "${YELLOW} Testing health endpoint...${NC}"
sleep 5

HEALTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/actuator/health)

if [ "$HEALTH_STATUS" == "200" ]; then
    echo -e "${GREEN} Health check passed!${NC}"
else
    echo -e "${RED}  Health check failed! Status: $HEALTH_STATUS${NC}"
fi

# Step 8: Display URLs
echo ""
echo -e "${GREEN} Deployment completed successfully!${NC}"
echo ""
echo " Service URLs:"
echo "  - API: http://localhost"
echo "  - Swagger UI: http://localhost/swagger-ui.html"
echo "  - API Docs: http://localhost/v3/api-docs"
echo "  - Health: http://localhost/actuator/health"
echo ""
echo " Useful commands:"
echo "  - View logs: docker-compose logs -f"
echo "  - View API logs: docker-compose logs -f tara-api"
echo "  - View Nginx logs: docker-compose logs -f nginx"
echo "  - Stop services: docker-compose down"
echo "  - Restart services: docker-compose restart"
echo "  - View status: docker-compose ps"