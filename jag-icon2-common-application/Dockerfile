FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk upgrade --no-cache

COPY ./target/jag-icon2-common-application.jar jag-icon2-common-application.jar

ENTRYPOINT ["java", "-Xmx1g", "-jar","/jag-icon2-common-application.jar"]
