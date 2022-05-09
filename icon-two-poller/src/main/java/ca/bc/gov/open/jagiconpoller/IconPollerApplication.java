package ca.bc.gov.open.jagiconpoller;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRabbit
@EnableScheduling
public class IconPollerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IconPollerApplication.class, args);
    }
}
