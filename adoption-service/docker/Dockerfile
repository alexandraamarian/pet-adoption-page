FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 3820

CMD ["java", "-jar", "app.jar"]