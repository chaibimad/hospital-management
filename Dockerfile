# Use Eclipse Temurin as the replacement for the deprecated openjdk image
FROM --platform=linux/amd64 eclipse-temurin:17-jdk

WORKDIR /app
COPY . .

# Ensure the Maven wrapper is executable and build the project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Use a more robust way to find the jar in case the filename changes
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]
