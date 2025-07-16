package com.test.whatsappapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaProducer {
    private static final String TOPIC = "chat-messages";

    private final KafkaTemplate<String, String> kafkaTemplate;
    public ChatKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String messageJson) {
        System.out.println(String.format("Sending message: %s", messageJson));
        kafkaTemplate.send(TOPIC, messageJson);
    }
}
