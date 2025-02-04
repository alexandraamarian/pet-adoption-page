FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 3520

CMD ["java", "-jar", "app.jar"]