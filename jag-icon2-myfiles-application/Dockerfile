FROM eclipse-temurin:11-jre-alpine

RUN apk upgrade expat  # Fix for CVE-2022-43680

COPY ./target/jag-icon2-myfile-application.jar jag-icon2-myfile-application.jar

ENTRYPOINT ["java", "-jar","/jag-icon2-myfile-application.jar"]
