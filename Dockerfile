FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR file
COPY target/tara-academy-api-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 9999

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9999/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]