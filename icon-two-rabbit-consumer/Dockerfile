FROM eclipse-temurin:11-jre-alpine

RUN apk upgrade expat  # Fix for CVE-2022-43680

COPY ./target/icon-two-rabbit-consumer.jar icon-two-rabbit-consumer.jar

ENTRYPOINT ["java", "-jar","/icon-two-rabbit-consumer.jar"]
