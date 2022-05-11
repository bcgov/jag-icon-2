package ca.bc.gov.open.jagiconconsumer.services;

import ca.bc.gov.open.icon.models.HSRModel;
import ca.bc.gov.open.icon.models.PingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class HsrConsumerService {

    @RabbitListener(queues = "${icon.hsr-queue}")
    public void receivedMessage(@Payload Message<HSRModel> message) throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.ping-queue}")
    public void receivedTestMessage(@Payload Message<PingModel> message)
            throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }
}
