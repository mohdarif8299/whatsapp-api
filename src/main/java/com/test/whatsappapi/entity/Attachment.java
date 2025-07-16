package com.test.whatsappapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    private String filePath;
    private String fileType;
    private Integer fileSize;
}