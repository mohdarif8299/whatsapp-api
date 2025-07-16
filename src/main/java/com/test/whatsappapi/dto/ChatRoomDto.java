package com.test.whatsappapi.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ChatRoomDto {
    private Long id;
    private String name;
    private boolean isGroup;
    private Set<Long> userIds;
}