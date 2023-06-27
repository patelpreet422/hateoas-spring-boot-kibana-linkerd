FROM openjdk:18
LABEL authors="Preet Patel"
ARG JAR=build/libs/userapi-0.0.1-SNAPSHOT.jar
COPY ${JAR} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app.jar"]
