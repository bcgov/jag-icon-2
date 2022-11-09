FROM eclipse-temurin:11-jre-alpine

RUN apk upgrade expat  # Fix for CVE-2022-43680

COPY ./target/icon-two-biometrics-application.jar icon-two-biometrics-application.jar

ENTRYPOINT ["java", "-jar","/icon-two-biometrics-application.jar"]
