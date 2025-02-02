FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 2660

CMD ["java", "-jar", "app.jar"]