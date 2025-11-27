# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -DskipTests

COPY src src
RUN ./mvnw clean package -DskipTests


# -------- RUN STAGE --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/bharatpulse-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
