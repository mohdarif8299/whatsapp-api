package com.test.whatsappapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.whatsappapi.dto.MessageSendRequest;
import com.test.whatsappapi.dto.MessageResponse;
import com.test.whatsappapi.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api/chatrooms/{chatRoomId}/messages")
@Tag(name = "Messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<MessageResponse> sendTextMessage(
            @PathVariable Long chatRoomId,
            @RequestBody MessageSendRequest request) throws Exception {
        return ResponseEntity.ok(
                messageService.sendMessageWithAttachment(chatRoomId, request, null)
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendMessageWithAttachment(
            @PathVariable Long chatRoomId,
            @RequestPart("message") String message,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        System.out.println("Working dir: " + System.getProperty("user.dir"));
        MessageSendRequest request = new ObjectMapper().readValue(message, MessageSendRequest.class);
        return ResponseEntity.ok(
                messageService.sendMessageWithAttachment(chatRoomId, request, file)
        );
    }

    @GetMapping
    public Page<MessageResponse> listMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return messageService.listMessages(chatRoomId, page, size);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {
        messageService.deleteMessage(chatRoomId, messageId);
        return ResponseEntity.noContent().build();
    }

}
