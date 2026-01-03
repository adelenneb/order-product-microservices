FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -pl services/eureka-server -am clean package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/services/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
