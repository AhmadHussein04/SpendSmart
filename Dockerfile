FROM amazoncorretto:17

# Set environment variables
ENV APP_HOME=/app

# Create application directory
WORKDIR $APP_HOME

# Copy the JAR file into the container
COPY target/SpendSmart-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]