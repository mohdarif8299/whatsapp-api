package com.test.whatsappapi.dto;

import lombok.Data;

@Data
public class AttachmentDto {
    private Long id;
    private String filePath;
    private String fileType;
    private Integer fileSize;
}