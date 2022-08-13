package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class ReportingController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportingController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "eReportAnswersSubmitted")
    @ResponsePayload
    public EReportAnswersSubmittedResponse eReportAnswersSubmitted(
            @RequestPayload EReportAnswersSubmitted eReportAnswersSubmitted)
            throws JsonProcessingException {

        var inner =
                eReportAnswersSubmitted.getEReportAnswers() != null
                        ? eReportAnswersSubmitted.getEReportAnswers()
                        : new EReportAnswers();

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/ereport-answers-submitted");
        HttpEntity<EReportAnswers> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "eReportAnswersSubmitted")));
            EReportAnswersSubmittedResponse out = new EReportAnswersSubmittedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "eReportAnswersSubmitted",
                                    ex.getMessage(),
                                    inner)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getReportingCmpltInstruction")
    @ResponsePayload
    public GetReportingCmpltInstructionResponse getReportingCmpltInstruction(
            @RequestPayload GetReportingCmpltInstruction getReportingCmpltInstruction)
            throws JsonProcessingException {

        var getReportingCmpltInstructionDocument =
                XMLUtilities.convertReq(
                        getReportingCmpltInstruction,
                        new GetReportingCmpltInstructionDocument(),
                        "getReportingCmpltInstruction");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/complete-instruction");
        HttpEntity<GetReportingCmpltInstructionDocument> payload =
                new HttpEntity<>(getReportingCmpltInstructionDocument, new HttpHeaders());

        try {
            HttpEntity<ReportingCmpltInstruction> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            ReportingCmpltInstruction.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "getReportingCmpltInstruction")));

            GetReportingCmpltInstructionResponseDocument
                    getReportingCmpltInstructionResponseDocument =
                            new GetReportingCmpltInstructionResponseDocument();
            ReportingCmpltInstructionOuter outResp = new ReportingCmpltInstructionOuter();
            outResp.setReportingCmpltInstruction(resp.getBody());
            getReportingCmpltInstructionResponseDocument.setXMLString(outResp);

            var getReportingCmpltInstructionResponse =
                    XMLUtilities.convertResp(
                            getReportingCmpltInstructionResponseDocument,
                            new GetReportingCmpltInstructionResponse(),
                            "getReportingCmpltInstructionResponse");

            return getReportingCmpltInstructionResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getReportingCmpltInstruction",
                                    ex.getMessage(),
                                    getReportingCmpltInstruction)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getLocations")
    @ResponsePayload
    public GetLocationsResponse getLocationsResponse(@RequestPayload GetLocations getLocations)
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {

        var getLocationsDocument =
                XMLUtilities.convertReq(getLocations, new GetLocationsDocument(), "getLocations");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/locations");
        HttpEntity<GetLocationsDocument> payload =
                new HttpEntity<>(getLocationsDocument, new HttpHeaders());

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocationsResponse")));

            GetLocationsResponseDocument getLocationsResponseDoc =
                    new GetLocationsResponseDocument();
            LocationsOuter outResp = new LocationsOuter();
            outResp.setLocations(resp.getBody());
            getLocationsResponseDoc.setXMLString(outResp);

            var getLocationsResponse =
                    XMLUtilities.convertResp(
                            getLocationsResponseDoc,
                            new GetLocationsResponse(),
                            "getLocationsResponse");

            return getLocationsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getLocationsResponse",
                                    ex.getMessage(),
                                    getLocations)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "submitAnswers")
    @ResponsePayload
    public SubmitAnswersResponse submitAnswers(@RequestPayload SubmitAnswers submitAnswers)
            throws JsonProcessingException {

        var submitAnswersDocument =
                XMLUtilities.convertReq(
                        submitAnswers, new SubmitAnswersDocument(), "submitAnswers");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/submit-answers");
        HttpEntity<SubmitAnswersDocument> payload =
                new HttpEntity<>(submitAnswersDocument, new HttpHeaders());

        try {
            HttpEntity<Ereport> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Ereport.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "submitAnswers")));

            SubmitAnswersResponseDocument submitAnswersResponseDocument =
                    new SubmitAnswersResponseDocument();
            ReportOuter outResp = new ReportOuter();
            outResp.setEReport(resp.getBody());
            submitAnswersResponseDocument.setXMLString(outResp);

            var submitAnswersResponse =
                    XMLUtilities.convertResp(
                            submitAnswersResponseDocument,
                            new SubmitAnswersResponse(),
                            "submitAnswersResponse");

            return submitAnswersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "submitAnswers",
                                    ex.getMessage(),
                                    submitAnswers)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getAppointment")
    @ResponsePayload
    public GetAppointmentResponse getAppointment(@RequestPayload GetAppointment getAppointment)
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {

        var getAppointmentDocument =
                XMLUtilities.convertReq(
                        getAppointment, new GetAppointmentDocument(), "getAppointment");

        HttpEntity<GetAppointmentDocument> payload =
                new HttpEntity<>(getAppointmentDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/appointment");

        try {
            HttpEntity<Appointment> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Appointment.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getAppointment")));

            GetAppointmentResponseDocument getAppointmentResponseDoc =
                    new GetAppointmentResponseDocument();
            AppointmentOuter outResp = new AppointmentOuter();
            outResp.setAppointment(resp.getBody());
            getAppointmentResponseDoc.setXMLString(outResp);

            var getAppointmentResponse =
                    XMLUtilities.convertResp(
                            getAppointmentResponseDoc,
                            new GetAppointmentResponse(),
                            "getAppointmentResponse");

            return getAppointmentResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getAppointment",
                                    ex.getMessage(),
                                    getAppointment)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getQuestions")
    @ResponsePayload
    public GetQuestionsResponse getQuestions(@RequestPayload GetQuestions getQuestions)
            throws JsonProcessingException {

        var getQuestionsDocument =
                XMLUtilities.convertReq(getQuestions, new GetQuestionsDocument(), "getQuestions");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/questions");
        HttpEntity<GetQuestionsDocument> payload =
                new HttpEntity<>(getQuestionsDocument, new HttpHeaders());

        try {
            HttpEntity<Ereport> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Ereport.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getQuestions")));

            GetQuestionsResponseDocument getQuestionsResponseDocument =
                    new GetQuestionsResponseDocument();
            ReportOuter outResp = new ReportOuter();
            outResp.setEReport(resp.getBody());
            getQuestionsResponseDocument.setXMLString(outResp);

            var getQuestionsResponse =
                    XMLUtilities.convertResp(
                            getQuestionsResponseDocument,
                            new GetQuestionsResponse(),
                            "getQuestionsResponse");

            return getQuestionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getQuestions",
                                    ex.getMessage(),
                                    getQuestions)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getStatus")
    @ResponsePayload
    public GetStatusResponse getStatus(@RequestPayload GetStatus getStatus)
            throws JsonProcessingException {

        var getStatusDocument =
                XMLUtilities.convertReq(getStatus, new GetStatusDocument(), "getStatus");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "reporting/status");
        HttpEntity<GetStatusDocument> payload =
                new HttpEntity<>(getStatusDocument, new HttpHeaders());

        try {
            HttpEntity<ca.bc.gov.open.icon.ereporting.Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            ca.bc.gov.open.icon.ereporting.Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getStatus")));

            GetStatusResponseDocument getStatusResponseDoc = new GetStatusResponseDocument();
            StatusOuter outResp = new StatusOuter();
            outResp.setStatus(resp.getBody());
            getStatusResponseDoc.setXMLString(outResp);

            var getStatusResponse =
                    XMLUtilities.convertResp(
                            getStatusResponseDoc, new GetStatusResponse(), "getStatusResponse");

            return getStatusResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getStatus",
                                    ex.getMessage(),
                                    getStatus)));
            throw new ORDSException();
        }
    }
}
