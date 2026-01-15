FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/stock-insight-api-0.0.1-SNAPSHOT.jar"]
