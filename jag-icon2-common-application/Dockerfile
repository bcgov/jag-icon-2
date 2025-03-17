FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --upgrade --no-cache expat  # fix CVE-2024-8176

COPY ./target/jag-icon2-common-application.jar jag-icon2-common-application.jar

ENTRYPOINT ["java", "-Xmx1g", "-jar","/jag-icon2-common-application.jar"]
