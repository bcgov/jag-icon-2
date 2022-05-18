package ca.bc.gov.open.jagiconconsumer.services;

import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Map;

@Service
@Slf4j
public class PACService {
    @Value("${icon.host}")
    private String cms_host = "https://127.0.0.1/";

    @Value("${icon.cmsint-host}")
    private String cmsint_host = "https://127.0.0.1/";

    @Value("${icon.pac-service-url}")
    private String pacServiceUrl = "https://127.0.0.1/";

    private final WebServiceTemplate webServiceTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public PACService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            WebServiceTemplate webServiceTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webServiceTemplate = webServiceTemplate;
    }

    // PACUpdate BPM
    public void processPAC(Client client) throws JsonProcessingException {
        // Get event type code
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(cmsint_host + "event-type")
                        .queryParam("clientNumber", client.getClientNumber())
                        .queryParam("eventSeqNum", client.getEventSeqNum());
        try {
            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});
            client.setEventTypeCode(resp.getBody().get("eventTypeCode"));
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getEventType")));
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getEventType",
                                    ex.getMessage(),
                                    client)));
            throw new ORDSException();
        }

        builder = UriComponentsBuilder.fromHttpUrl(cms_host + "pac/update");
        try {
            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(client, new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "pacUpdate")));
            String status = resp.getBody().get("status");
            if (status.equals("0")) {
                log.info("PAC update success");
            } else {
                log.info("PAC update cancel");
            }
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "pacUpdate",
                                    ex.getMessage(),
                                    client)));
            throw new ORDSException();
        }
        // End of BPM
    }
}
