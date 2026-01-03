FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -pl services/config-server -am clean package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/services/config-server/target/config-server-0.0.1-SNAPSHOT.jar app.jar
COPY config-repo /app/config-repo
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app/app.jar"]
