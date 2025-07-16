package com.test.whatsappapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class WhatsappApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsappApiApplication.class, args);
    }

}
