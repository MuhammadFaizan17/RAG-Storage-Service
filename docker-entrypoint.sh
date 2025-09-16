#!/bin/sh

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
while ! nc -z postgres 5432; do
  sleep 1
done
echo "PostgreSQL is ready"

# Run Liquibase migrations
echo "Running database migrations..."
java -jar app.jar --spring.config.location=classpath:/application.properties liquibase:update

# Start the application
echo "Starting the application..."
exec java -jar app.jar
