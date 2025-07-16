package com.test.whatsappapi.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageResponse {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private String messageType;
    private LocalDateTime sentAt;
    private Long replyToId;
    private List<AttachmentDto> attachments;
}