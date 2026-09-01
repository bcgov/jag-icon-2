FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk upgrade --no-cache

COPY ./target/jag-icon2-biometrics-application.jar jag-icon2-biometrics-application.jar

ENTRYPOINT ["java", "-jar","/jag-icon2-biometrics-application.jar"]
