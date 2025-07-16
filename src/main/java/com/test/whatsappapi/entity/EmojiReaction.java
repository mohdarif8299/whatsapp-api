package com.test.whatsappapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emoji_reactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmojiReaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String emoji; // thumbup, love, crying, surprised
    private LocalDateTime reactedAt;
}