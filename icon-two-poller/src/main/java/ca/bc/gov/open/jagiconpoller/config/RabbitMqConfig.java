package ca.bc.gov.open.jagiconpoller.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitMqConfig {

    private final QueueConfig queueConfig;

    @Autowired
    public RabbitMqConfig(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "pac-queue")
    public Queue pacQueue() {
        return new Queue(queueConfig.getPacQueueName(), false);
    }

    @Bean(name = "ping-queue")
    public Queue testQueue() {
        return new Queue(queueConfig.getPingQueueName(), false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(queueConfig.getTopicExchangeName());
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Declarables binding(
            @Qualifier("pac-queue") Queue pacQueue,
            @Qualifier("ping-queue") Queue testQueue,
            DirectExchange exchange) {
        return new Declarables(
                BindingBuilder.bind(pacQueue).to(exchange).with(queueConfig.getPacRoutingkey()),
                BindingBuilder.bind(testQueue).to(exchange).with(queueConfig.getPingRoutingKey()));
    }
}
