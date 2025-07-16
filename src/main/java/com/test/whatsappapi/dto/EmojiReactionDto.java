package com.test.whatsappapi.dto;

import lombok.Data;

@Data
public class EmojiReactionDto {
    private Long id;
    private Long messageId;
    private Long userId;
    private String emoji;
}