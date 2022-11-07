FROM eclipse-temurin:11-jre-alpine

RUN apk upgrade expat  # Fix for CVE-2022-43680

COPY ./target/icon-two-common-application-1-1.jar icon-two-common-application-1-1.jar

ENTRYPOINT ["java", "-jar","/icon-two-common-application-1-1.jar"]
