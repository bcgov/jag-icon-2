# jag-icon-2

[![Lifecycle:Experimental](https://img.shields.io/badge/Lifecycle-Experimental-339999)](https://github.com/bcgov/jag-icon-2)
[![Maintainability](https://api.codeclimate.com/v1/badges/a492f352f279a2d1621e/maintainability)](https://codeclimate.com/github/bcgov/jag-icon-2/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a492f352f279a2d1621e/test_coverage)](https://codeclimate.com/github/bcgov/jag-icon-2/test_coverage)

### Recommended Tools
* Intellij
* Docker
* Maven
* Java 11

### Application Endpoints

Local Host: http://127.0.0.1:8080

WSDL Endpoint Local:
* common-1-1:
* localhost:8080/common-1-1/ICON2.Source.EReporting.ws.provider:Audit?WSDL

* localhost:8080/common-1-1/ICON2.Source.MyInfo.ws.provider:MyInfo?WSDL

* localhost:8080/common-1-1/ICON2.Source.EReporting.ws.provider:EReporting?WSDL

* localhost:8080/common-1-1/ICON2.Source.Common.ws.provider:ErrorHandling?WSDL

* localhost:8080/common-1-1/ICON2.Source.HealthServiceRequest.ws.provider:HSR?WSDL

* localhost:8080/common-1-1/ICON2.Source.Message.ws.provider:Message?WSDL

* localhost:8080/common-1-1/ICON2.Source.Version.ws.provider:PackageInfo?WSDL

* localhost:8080/common-1-1/ICON2.Source.Common.ws.provider:SessionParameter?WSDL

* localhost:8080/common-1-1/ICON2.Source.TombStoneInfo.ws.provider:TombStoneInfo?WSDL

* localhost:8080/common-1-1/ICON2.Source.TrustAccount.ws.provider:TrustAccount?WSDL

* localhost:8080/common-1-1/ICON2.Source.VisitSchedule.ws.provider:VisitSchedule?WSDL

* Biometrics:

* localhost:8080/Biometrics/ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics?WSDL

* common-1-2:

* localhost:8080/common-1-2/ICON2.Source.Authorization.ws.provider:AuthAuth?WSDL

* MyFiles:

* localhost:8080/MyFiles/ICON2_MyFiles.Source.MyFiles.ws:MyFiles?WSDL

* Actuator Endpoint Local: http:
* //localhost:8080/actuator/health

### Required Environmental Variables

BASIC_AUTH_PASS: The password for the basic authentication. This can be any value for local.

BASIC_AUTH_USER: The username for the basic authentication. This can be any value for local.

ORDS_HOST: The url for ords rest package.

Additional Evn Variables
* common-application-1-1:
* PING_QUEUE_NAME
* PING_ROUTING_KEY
* HSR_SERVICE_URL
* HSR_QUEUE_NAME
* HSR_ROUTING_KEY
* RABBIT_EXCHANGE_NAME
* RABBIT_MQ_HOST
* RABBIT_MQ_USERNAME
* RABBIT_MQ_PASSWORD
* common-Comparison-Tool-1-1/Common-Comparison-Tool-1-2:
* ORACLE_HOST
* API_HOST
* WM_HOST
* hsr-application
* HSR_QUEUE_NAME
* HSR_SERVICE_URL
* RABBIT_MQ_HOST
* RABBIT_MQ_USERNAME
* RABBIT_MQ_PASSWORD
* biometrics-application:
* HSR_SERVICE_URL
* WS_AUTH_PASS
* WS_AUTH_USER
* BCS_HOST
* ONLINE_SERVICE_ID
* IPS_HOST
* IIS_HOST
* AutomatedTests
* API_HOST
* myfiles-application/common-application-1-2:
* none

### Optional Enviromental Variables
SPLUNK_HTTP_URL: The url for the splunk hec.

SPLUNK_TOKEN: The bearer token to authenticate the application.

SPLUNK_INDEX: The index that the application will push logs to. The index must be created in splunk
before they can be pushed to.

### Building the Application
1) Make sure using java 11 for the project modals and sdk
2) Run ```mvn compile```
3) Make sure ```target/generated-sources/xjc``` folder in included in module path

### Pre-running the application
Run ```docker run -p 5672:5672 -p 15672:15672 rabbitmq:management```

### Running the application
Via IDE
1) Set env variables.
2) Run the application

Via Jar, ex. to run 'icon-two-common-application-1-1' application
1) Set env variables.
2) Run ```mvn package```
3) Run ```cd icon-two-common-application-1-1```
4) Run ```java -jar ./target/icon-two-common-application-1-1.jar```

Via Docker in an application, ex. to run 'icon-two-common-application-1-1' application
1) Set env variables.
2) Run ```mvn package```
3) Run ```cd icon-two-common-application-1-1```
4) Run ```docker build -t icon-two-common-application-1-1 .``` from root folder
5) Run ```docker run -p 8080:8080 icon-two-common-application-1-1```

### Running RabbitMQ
* http://localhost:15672/
* Username: 'guest' by default
* Password: 'guest' by default

### Pre Commit
1) Do not commit \CRLF use unix line enders
2) Run the linter ```mvn spotless:apply```

### JaCoCo Coverage Report
1) Run ```mvn test```
2) Run ```mvn jacoco:report```
3) Open ```target/site/jacoco/index.html``` in a browser
