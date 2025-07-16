package com.test.whatsappapi.controller;

import com.test.whatsappapi.dto.EmojiReactionDto;
import com.test.whatsappapi.service.EmojiReactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages/{messageId}/emoji")
public class EmojiReactionController {

    private final EmojiReactionService emojiReactionService;

    public EmojiReactionController(EmojiReactionService emojiReactionService) {
        this.emojiReactionService = emojiReactionService;
    }

    @PostMapping
    public ResponseEntity<EmojiReactionDto> react(@PathVariable Long messageId,
                                                  @RequestParam Long userId,
                                                  @RequestParam String emoji) {
        return ResponseEntity.ok(emojiReactionService.react(messageId, userId, emoji));
    }

    @GetMapping
    public ResponseEntity<Page<EmojiReactionDto>> getAllReactions(
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(emojiReactionService.getReactionsForMessage(messageId, page, size));
    }

}