package com.test.whatsappapi.service;

import com.test.whatsappapi.dto.EmojiReactionDto;
import com.test.whatsappapi.entity.EmojiReaction;
import com.test.whatsappapi.entity.Message;
import com.test.whatsappapi.entity.User;
import com.test.whatsappapi.exception.ApiException;
import com.test.whatsappapi.repository.EmojiReactionRepository;
import com.test.whatsappapi.repository.MessageRepository;
import com.test.whatsappapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmojiReactionService {

    private static final Set<String> ALLOWED_EMOJIS = Set.of("thumbup", "love", "crying", "suprised");

    private final EmojiReactionRepository emojiReactionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public EmojiReactionService(EmojiReactionRepository emojiReactionRepository,  MessageRepository messageRepository, UserRepository userRepository) {
        this.emojiReactionRepository = emojiReactionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public EmojiReactionDto react(Long messageId, Long userId, String emoji) {
        if (emoji == null || emoji.trim().isEmpty())
            throw new ApiException("Emoji is required");
        String normalizedEmoji = emoji.trim().toLowerCase();
        if (!ALLOWED_EMOJIS.contains(normalizedEmoji))
            throw new ApiException("Unsupported emoji reaction: " + emoji);

        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException("Message not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        EmojiReaction existing = emojiReactionRepository
                .findByMessageIdAndUserId(messageId, userId)
                .orElse(null);

        EmojiReaction reaction;
        if (existing != null) {
            existing.setEmoji(normalizedEmoji);
            existing.setReactedAt(LocalDateTime.now());
            reaction = emojiReactionRepository.save(existing);
        } else {
            reaction = EmojiReaction.builder()
                    .message(msg)
                    .user(user)
                    .emoji(normalizedEmoji)
                    .reactedAt(LocalDateTime.now())
                    .build();
            reaction = emojiReactionRepository.save(reaction);
        }

        EmojiReactionDto dto = new EmojiReactionDto();
        dto.setId(reaction.getId());
        dto.setMessageId(msg.getId());
        dto.setUserId(user.getId());
        dto.setEmoji(reaction.getEmoji());
        return dto;
    }

    public Page<EmojiReactionDto> getReactionsForMessage(Long messageId, int page, int size) {
        if (!messageRepository.existsById(messageId)) {
            throw new ApiException("Message not found");
        }
        Page<EmojiReaction> pageData = emojiReactionRepository.findByMessageId(messageId, PageRequest.of(page, size));
        return pageData.map(reaction -> {
            EmojiReactionDto dto = new EmojiReactionDto();
            dto.setId(reaction.getId());
            dto.setMessageId(reaction.getMessage().getId());
            dto.setUserId(reaction.getUser().getId());
            dto.setEmoji(reaction.getEmoji());
            return dto;
        });
    }

}
