package com.test.whatsappapi.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaConsumer {

    @KafkaListener(topics = "chat-messages", groupId = "whatsapp-group")
    public void consume(String messageJson) {
        System.out.println("Received from Kafka: " + messageJson);
    }
}