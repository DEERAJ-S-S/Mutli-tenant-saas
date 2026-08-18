# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build final jar file
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Runtime Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]