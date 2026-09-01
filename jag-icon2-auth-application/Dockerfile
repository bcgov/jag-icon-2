FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk upgrade --no-cache

COPY ./target/jag-icon2-auth-application.jar jag-icon2-auth-application.jar

ENTRYPOINT ["java", "-Xmx1g", "-jar","/jag-icon2-auth-application.jar"]
