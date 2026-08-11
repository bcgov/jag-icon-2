FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --upgrade --no-cache expat p11-kit  # fix CVE-2024-8176 (expat), CVE-2026-2100 (p11-kit)

COPY ./target/jag-icon2-biometrics-application.jar jag-icon2-biometrics-application.jar

ENTRYPOINT ["java", "-jar","/jag-icon2-biometrics-application.jar"]
