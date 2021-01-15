FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/customersApi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9098
ENTRYPOINT ["java", "-jar", "/app.jar"]