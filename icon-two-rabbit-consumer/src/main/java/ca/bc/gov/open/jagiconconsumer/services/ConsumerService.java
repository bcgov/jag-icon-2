package ca.bc.gov.open.jagiconconsumer.services;

import ca.bc.gov.open.icon.models.Client;
import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.icon.models.PACModel;
import ca.bc.gov.open.icon.models.PingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerService {
    private final HSRService hsrService;
    private final PACService pacService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ConsumerService(ObjectMapper objectMapper, HSRService hsrService, PACService pacService) {
        this.objectMapper = objectMapper;
        this.hsrService = hsrService;
        this.pacService = pacService;
    }

    @RabbitListener(queues = "${icon.hsr-queue}")
    public void receiveHSRMessage(@Payload Message<HealthServicePub> message)
            throws IOException {
        try {
            hsrService.processHSR(message.getPayload());
        } catch (Exception ignored) {
            log.error("HSR BPM ERROR: " + message + " not processed successfully");
        }
        System.out.println(objectMapper.writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.pac-queue}")
    public void receivePACMessage(@Payload Message<Client> message)
        throws IOException {
        try {
            pacService.processPAC(message.getPayload());
        } catch (Exception ignored) {
            log.error("PAC BPM ERROR: " + message + " not processed successfully");
        }
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.ping-queue}")
    public void receivePingMessage(@Payload Message<PingModel> message)
            throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }
}
