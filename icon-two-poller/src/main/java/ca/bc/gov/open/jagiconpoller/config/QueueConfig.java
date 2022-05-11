package ca.bc.gov.open.jagiconpoller.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class QueueConfig {

    @Value("${icon.exchange-name}")
    private String topicExchangeName;

    @Value("${icon.pac-queue}")
    private String pacQueueName;

    @Value("${icon.ping-queue}")
    private String pingQueueName;

    @Value("${icon.pac-routing-key}")
    private String pacRoutingkey;

    @Value("${icon.ping-routing-key}")
    private String pingRoutingKey;
}
