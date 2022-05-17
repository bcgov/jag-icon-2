package ca.bc.gov.open.jagiconpoller.services;

import ca.bc.gov.open.icon.models.PACModel;
import ca.bc.gov.open.icon.models.PingModel;
import ca.bc.gov.open.jagiconpoller.config.QueueConfig;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class PACPollerService {

    @Value("${icon.ords-host}")
    private String ordsHost = "https://127.0.0.1/";

    private final Queue pacQueue;

    private final Queue pingQueue;

    private final RestTemplate restTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final AmqpAdmin amqpAdmin;

    private final QueueConfig queueConfig;

    public PACPollerService(
            @Qualifier("pac-queue") Queue pacQueue,
            @Qualifier("ping-queue") Queue pingQueue,
            RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate,
            AmqpAdmin amqpAdmin,
            QueueConfig queueConfig) {
        this.pacQueue = pacQueue;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.pingQueue = pingQueue;
        this.queueConfig = queueConfig;
    }

    @PostConstruct
    public void createQueues() {
        amqpAdmin.declareQueue(pacQueue);
        amqpAdmin.declareQueue(pingQueue);
    }

    private void sendToRabbitMq(List<PACModel> items) {
        items.forEach(
                i ->
                        this.rabbitTemplate.convertAndSend(
                                queueConfig.getTopicExchangeName(),
                                queueConfig.getPacRoutingkey(),
                                i));
    }

    @Scheduled(fixedDelay = 5000)
    private void ping() {
        PingModel p = new PingModel();
        p.setSource("ping");
        log.info("Sending ping with source " + p.getSource());
        this.rabbitTemplate.convertAndSend(
                queueConfig.getTopicExchangeName(), queueConfig.getPingRoutingKey(), p);
    }

    //  Scheduled every minute in MS
    @Scheduled(fixedDelay = 60 * 1000)
    private void pollOrdsForNewRecords() {
        log.info("Polling db for new records");

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ordsHost + "pac/poll");

            HttpEntity<List<PACModel>> resp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            if (resp.hasBody()) {
                log.info("Pulled " + resp.getBody().size() + " new records");
                sendToRabbitMq(resp.getBody());
            }
        } catch (Exception ex) {
            log.error("Failed to pull new records from the db: " + ex.getMessage());
        }
    }
}
