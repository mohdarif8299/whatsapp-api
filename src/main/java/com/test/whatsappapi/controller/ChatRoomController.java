package com.test.whatsappapi.controller;

import com.test.whatsappapi.dto.ChatRoomDto;
import com.test.whatsappapi.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ResponseEntity<ChatRoomDto> create(@RequestBody ChatRoomDto req) {
        return ResponseEntity.ok(chatRoomService.create(req));
    }

    @GetMapping
    public Page<ChatRoomDto> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return chatRoomService.list(page, size);
    }
}