FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew
RUN ./gradlew bootJar -x test --no-daemon --stacktrace --info


EXPOSE 8080

CMD ["java", "-jar", "build/libs/stock-insight-api-0.0.1-SNAPSHOT.jar"]
