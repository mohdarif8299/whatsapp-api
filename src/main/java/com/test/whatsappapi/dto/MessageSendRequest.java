package com.test.whatsappapi.dto;


import lombok.Data;

@Data
public class MessageSendRequest {
    private Long senderId;
    private String content;
    private String messageType;
    private Long replyToId;
}