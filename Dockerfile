# Use the official Maven image to build the app
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Create and change to the app directory.
WORKDIR /app

# Copy pom.xml and install dependencies first for better caching
COPY pom.xml .
COPY src ./src

# Build the app
RUN mvn -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean package

# Use the Eclipse Temurin image for the runtime
FROM eclipse-temurin:21-alpine

# Create and change to the app directory.
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]