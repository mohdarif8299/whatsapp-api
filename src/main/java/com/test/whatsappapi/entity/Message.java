package com.test.whatsappapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;
    private LocalDateTime sentAt;

    private String messageType;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private Message replyTo;


    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<EmojiReaction> reactions;
}