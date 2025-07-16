package com.test.whatsappapi.repository;

import com.test.whatsappapi.entity.EmojiReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmojiReactionRepository extends JpaRepository<EmojiReaction, Long> {
    Optional<EmojiReaction> findByMessageIdAndUserId(Long messageId, Long userId);
    Page<EmojiReaction> findByMessageId(Long messageId, Pageable pageable);
}
