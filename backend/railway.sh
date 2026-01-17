#!/bin/bash

# Railway startup script
# This script ensures Railway environment variables are properly set

echo "🚀 Starting Railway deployment..."

# Check if Railway environment variables exist and set them if needed
if [ -z "$RAILWAY_PGHOST" ] && [ -n "$PGHOST" ]; then
    export RAILWAY_PGHOST="$PGHOST"
    echo "✅ Set RAILWAY_PGHOST from PGHOST: $PGHOST"
fi

if [ -z "$RAILWAY_PGPORT" ] && [ -n "$PGPORT" ]; then
    export RAILWAY_PGPORT="$PGPORT"
    echo "✅ Set RAILWAY_PGPORT from PGPORT: $PGPORT"
fi

if [ -z "$RAILWAY_PGDATABASE" ] && [ -n "$PGDATABASE" ]; then
    export RAILWAY_PGDATABASE="$PGDATABASE"
    echo "✅ Set RAILWAY_PGDATABASE from PGDATABASE: $PGDATABASE"
fi

if [ -z "$RAILWAY_PGUSER" ] && [ -n "$PGUSER" ]; then
    export RAILWAY_PGUSER="$PGUSER"
    echo "✅ Set RAILWAY_PGUSER from PGUSER: $PGUSER"
fi

if [ -z "$RAILWAY_PGPASSWORD" ] && [ -n "$PGPASSWORD" ]; then
    export RAILWAY_PGPASSWORD="$PGPASSWORD"
    echo "✅ Set RAILWAY_PGPASSWORD from PGPASSWORD"
fi

# Set JWT secret if not provided
if [ -z "$JWT_SECRET" ]; then
    export JWT_SECRET="mySecretKey123456789012345678901234567890"
    echo "✅ Set default JWT_SECRET"
fi

if [ -z "$JWT_EXPIRATION" ]; then
    export JWT_EXPIRATION="86400000"
    echo "✅ Set default JWT_EXPIRATION"
fi

echo "🔗 Database URL: jdbc:postgresql://${RAILWAY_PGHOST}:${RAILWAY_PGPORT}/${RAILWAY_PGDATABASE}"
echo "👤 Database User: ${RAILWAY_PGUSER}"

# Start the Spring Boot application
echo "🎯 Starting Spring Boot application..."
java -jar target/product-review-backend-1.0.0.jar --spring.profiles.active=railway
