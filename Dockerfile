# What Docker is doing in plain English:
# Docker is like a digital shipping container. Instead of installing Java, Maven, and all the dependencies manually on every server, Docker packs the entire application and everything it needs to run into one single "Image" box. You can then drop this box onto any computer in the world, and it will run exactly the same way without installation headaches.

# What "multi-stage build" means and why it makes the image smaller:
# Building a Java app requires heavy tools like Maven and the full Java Development Kit (JDK), which take up hundreds of megabytes. However, RUNNING the app only requires a lightweight Java Runtime Environment (JRE). 
# Multi-stage builds act like a factory assembly line: 
# Stage 1 (Builder): We bring in the heavy Maven tools, build the .jar file, and leave all the heavy junk behind.
# Stage 2 (Runner): We take ONLY the finished .jar file from Stage 1 and put it in a tiny, fresh JRE container. This makes our final shipping box drastically smaller, faster to download, and much more secure.

# --- Stage 1: Build the Application ---
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src
# Compile the code and package it into a .jar file, skipping tests to speed up the build
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy ONLY the compiled .jar file from the 'builder' stage above
COPY --from=builder /app/target/auction-platform-0.0.1-SNAPSHOT.jar app.jar
# Expose the port the app runs on
EXPOSE 8080
# The command that starts the app when the container boots up
ENTRYPOINT ["java", "-jar", "app.jar"]
