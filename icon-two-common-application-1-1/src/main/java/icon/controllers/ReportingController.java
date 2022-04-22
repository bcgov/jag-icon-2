package icon.controllers;

import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.ereporting.Locations.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/complete-instruction");
        HttpEntity<GetReportingCmpltInstruction> payload =
                new HttpEntity<>(getReportingCmpltInstruction, new HttpHeaders());

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

            GetReportingCmpltInstructionResponse getReportingCmpltInstructionResponse =
                    new GetReportingCmpltInstructionResponse();
            ReportingCmpltInstructionOuter outResp = new ReportingCmpltInstructionOuter();
            ReportingCmpltInstructionInner inResp = new ReportingCmpltInstructionInner();
            inResp.setReportingCmpltInstruction(resp.getBody());
            outResp.setReportingCmpltInstruction(inResp);
            getReportingCmpltInstructionResponse.setXMLString(outResp);
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
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/locations");
        HttpEntity<GetLocations> payload = new HttpEntity<>(getLocations, new HttpHeaders());

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocationsResponse")));

            GetLocationsResponse getLocationsResponse = new GetLocationsResponse();
            LocationsOuter outResp = new LocationsOuter();
            LocationsInner inResp = new LocationsInner();
            inResp.setLocations(resp.getBody());
            outResp.setLocations(inResp);
            getLocationsResponse.setXMLString(outResp);
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

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/submit-answers");
        HttpEntity<SubmitAnswers> payload = new HttpEntity<>(submitAnswers, new HttpHeaders());

        try {
            HttpEntity<Report> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Report.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "submitAnswers")));

            SubmitAnswersResponse submitAnswersResponse = new SubmitAnswersResponse();
            ReportOuter outResp = new ReportOuter();
            ReportInner inResp = new ReportInner();
            inResp.setEReport(resp.getBody());
            outResp.setReport(inResp);
            submitAnswersResponse.setXMLString(outResp);
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
            throws JsonProcessingException {

        HttpEntity<GetAppointment> payload = new HttpEntity<>(getAppointment, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/appointment");

        try {

            HttpEntity<Appointment> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Appointment.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getAppointment")));

            GetAppointmentResponse getClientHistoryResponse = new GetAppointmentResponse();
            AppointmentOuter outResp = new AppointmentOuter();
            AppointmentInner inResp = new AppointmentInner();
            inResp.setAppointment(resp.getBody());
            outResp.setAppointment(inResp);
            getClientHistoryResponse.setXMLString(outResp);
            return getClientHistoryResponse;
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

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/questions");
        HttpEntity<GetQuestions> payload = new HttpEntity<>(getQuestions, new HttpHeaders());

        try {
            HttpEntity<Report> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Report.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getQuestions")));

            GetQuestionsResponse getQuestionsResponse = new GetQuestionsResponse();
            ReportOuter outResp = new ReportOuter();
            ReportInner inResp = new ReportInner();
            inResp.setEReport(resp.getBody());
            outResp.setReport(inResp);
            getQuestionsResponse.setXMLString(outResp);
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

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "reporting/status");
        HttpEntity<GetStatus> payload = new HttpEntity<>(getStatus, new HttpHeaders());

        try {
            HttpEntity<ca.bc.gov.open.icon.ereporting.Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            ca.bc.gov.open.icon.ereporting.Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getQuestions")));

            GetStatusResponse getStatusResponse = new GetStatusResponse();
            StatusOuter outResp = new StatusOuter();
            StatusInner inResp = new StatusInner();
            inResp.setStatus(resp.getBody());
            outResp.setStatus(inResp);
            getStatusResponse.setXMLString(outResp);
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
