##############################################################################################
#### Stage where the maven dependencies are cached                                         ###
##############################################################################################
FROM maven:3.8-eclipse-temurin-11 as dependencies-cache

ARG MVN_PROFILE

WORKDIR /build

## for the lack at a COPY --patern */pom.xml, we have to declare all the pom files manually
COPY ./pom.xml pom.xml
COPY ./bom/icon-two-starters-bom/pom.xml bom/icon-two-starters-bom/pom.xml
COPY ./AutomatedTests/pom.xml AutomatedTests/pom.xml
COPY ./icon-two-biometrics-application/pom.xml icon-two-biometrics-application/pom.xml
COPY ./icon-two-comparison-tool-1-2/pom.xml icon-two-comparison-tool-1-2/pom.xml
COPY ./icon-two-consumer-models/pom.xml icon-two-consumer-models/pom.xml
COPY ./icon-two-rabbit-consumer/pom.xml icon-two-rabbit-consumer/pom.xml
COPY ./icon-two-common-application-1-2/pom.xml icon-two-common-application-1-2/pom.xml
COPY ./icon-two-code-coverage/pom.xml icon-two-code-coverage/pom.xml
COPY ./icon-two-common-models/pom.xml icon-two-common-models/pom.xml
COPY ./icon-two-comparison-tool-1-1/pom.xml icon-two-comparison-tool-1-1/pom.xml
COPY ./icon-two-common-application-1-1/pom.xml icon-two-common-application-1-1/pom.xml
COPY ./icon-two-myfiles-application/pom.xml icon-two-myfiles-application/pom.xml

RUN  mvn dependency:go-offline \
    -P${MVN_PROFILE} \
    -DskipTests \
    --no-transfer-progress \
    --batch-mode \
    --fail-never

##############################################################################################
#### Stage where the application is built                                                  ###
##############################################################################################
FROM dependencies-cache as build

ARG MVN_PROFILE

WORKDIR /build

COPY ./AutomatedTests/src AutomatedTests/src
COPY ./icon-two-biometrics-application/src icon-two-biometrics-application/src
COPY ./icon-two-comparison-tool-1-2/src icon-two-comparison-tool-1-2/src
COPY ./icon-two-consumer-models/src icon-two-consumer-models/src
COPY ./icon-two-rabbit-consumer/src icon-two-rabbit-consumer/src
COPY ./icon-two-common-application-1-2/src icon-two-common-application-1-2/src
COPY ./icon-two-code-coverage/lombok.config icon-two-code-coverage/lombok.config
COPY ./icon-two-common-models/src icon-two-common-models/src
COPY ./icon-two-comparison-tool-1-1/src icon-two-comparison-tool-1-1/src
COPY ./icon-two-common-application-1-1/src icon-two-common-application-1-1/src
COPY ./icon-two-myfiles-application/src icon-two-myfiles-application/src


RUN  mvn clean package \
     -P${MVN_PROFILE} \
     -DskipTests \
     --no-transfer-progress \
     --batch-mode

##############################################################################################
#### Stage where Docker is running a java process to run a service built in previous stage ###
##############################################################################################
FROM eclipse-temurin:11-jre-jammy

ARG MVN_PROFILE

COPY --from=build /build/${MVN_PROFILE}/target/${MVN_PROFILE}*.jar /app/application.jar

ENTRYPOINT ["java", "-jar","/application.jar"]
