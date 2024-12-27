# Use a lightweight Java image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the stub file into the container
COPY stubs.json /app/stubs.json

# Command to run the mock server (if applicable)
CMD ["echo", "Mock service is ready with stubs.json"]