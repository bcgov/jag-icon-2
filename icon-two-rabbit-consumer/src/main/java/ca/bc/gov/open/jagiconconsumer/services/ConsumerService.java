package ca.bc.gov.open.jagiconconsumer.services;

import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.icon.models.PACModel;
import ca.bc.gov.open.icon.models.PingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.SocketException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerService {
    private final HSRService hsrService;

    @Autowired
    public ConsumerService(HSRService hsrService) {
        this.hsrService = hsrService;
    }

    @RabbitListener(queues = "${icon.hsr-queue}")
    public void receiveHSRMessage(@Payload Message<HealthServicePub> message)
            throws JsonProcessingException, SocketException, InterruptedException {
        hsrService.processHSR(message.getPayload());
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.pac-queue}")
    public void receivePACMessage(@Payload Message<PACModel> message)
            throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.ping-queue}")
    public void receivePingMessage(@Payload Message<PingModel> message)
            throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }
}
