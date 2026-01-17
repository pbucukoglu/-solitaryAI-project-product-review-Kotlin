FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the jar file
COPY target/product-review-backend-1.0.0.jar app.jar

# Make the startup script executable
COPY railway.sh /railway.sh
RUN chmod +x /railway.sh

# Expose port
EXPOSE 8080

# Set Railway environment variables fallbacks
ENV RAILWAY_PGHOST=${PGHOST}
ENV RAILWAY_PGPORT=${PGPORT}
ENV RAILWAY_PGDATABASE=${PGDATABASE}
ENV RAILWAY_PGUSER=${PGUSER}
ENV RAILWAY_PGPASSWORD=${PGPASSWORD}
ENV JWT_SECRET=${JWT_SECRET:mySecretKey123456789012345678901234567890}
ENV JWT_EXPIRATION=${JWT_EXPIRATION:86400000}

# Start the application
CMD ["/railway.sh"]
