package com.test.whatsappapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.whatsappapi.dto.AttachmentDto;
import com.test.whatsappapi.dto.MessageResponse;
import com.test.whatsappapi.dto.MessageSendRequest;
import com.test.whatsappapi.entity.Attachment;
import com.test.whatsappapi.entity.ChatRoom;
import com.test.whatsappapi.entity.Message;
import com.test.whatsappapi.entity.User;
import com.test.whatsappapi.exception.ApiException;
import com.test.whatsappapi.repository.ChatRoomRepository;
import com.test.whatsappapi.repository.MessageRepository;
import com.test.whatsappapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    private final static Integer MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatKafkaProducer chatKafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${app.root.picture-dir}")
    private String pictureDir;

    @Value("${app.root.video-dir}")
    private String videoDir;

    public MessageService(MessageRepository messageRepository,
                          ChatRoomRepository chatRoomRepository,
                          UserRepository userRepository,
                          ChatKafkaProducer chatKafkaProducer,
                          ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.chatKafkaProducer = chatKafkaProducer;
        this.objectMapper = objectMapper;
    }


    public MessageResponse sendMessageWithAttachment(Long chatRoomId, MessageSendRequest request, MultipartFile file) throws IOException {
        if (request == null || request.getSenderId() == null || request.getMessageType() == null) {
            throw new ApiException("Invalid message request: senderId and messageType are required");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException("ChatRoom not found"));
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ApiException("Sender not found"));

        Message replyToMessage = null;
        if (request.getReplyToId() != null) {
            replyToMessage = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new ApiException("Reply to message not found"));
        }
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .messageType(request.getMessageType())
                .replyTo(replyToMessage)
                .build();


        if (file != null && !file.isEmpty()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ApiException("Attachment file size exceeds the 10 MB limit.");
            }

            String contentType = file.getContentType();
            String folder;
            if (contentType != null && contentType.startsWith("image/")) {
                folder = pictureDir;
            } else if (contentType != null && contentType.startsWith("video/")) {
                folder = videoDir;
            } else {
                throw new ApiException("Unsupported file type: " + contentType);
            }
            String absoluteFolder = Paths.get(folder).toAbsolutePath().toString();
            File dirFile = new File(absoluteFolder);
            if (!dirFile.exists()) dirFile.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(absoluteFolder, fileName);

            file.transferTo(dest);

            Attachment attachment = Attachment.builder()
                    .filePath(dest.getAbsolutePath())
                    .fileType(contentType)
                    .fileSize((int) file.getSize())
                    .message(message)
                    .build();

            message.setAttachments(List.of(attachment));
        }

        Message saved = messageRepository.save(message);
        MessageResponse resp = toDto(saved);

        try {
            String eventJson = objectMapper.writeValueAsString(resp);
            chatKafkaProducer.sendMessage(eventJson);
        } catch (Exception e) {
            System.err.println("Kafka publish failed: " + e.getMessage());
        }

        return resp;
    }


    public Page<MessageResponse> listMessages(Long chatRoomId, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        Page<Message> pg = messageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pr);

        List<MessageResponse> dtos = pg.stream().map(this::toDto).toList();
        return new PageImpl<>(dtos, pr, pg.getTotalElements());
    }

    public void deleteMessage(Long chatRoomId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException("Message not found"));
        if (!message.getChatRoom().getId().equals(chatRoomId)) {
            throw new ApiException("Message does not belong to the specified chat room");
        }
        messageRepository.delete(message);
    }

    private MessageResponse toDto(Message message) {
        MessageResponse resp = new MessageResponse();
        resp.setId(message.getId());
        resp.setChatRoomId(message.getChatRoom().getId());
        resp.setSenderId(message.getSender().getId());
        resp.setSenderUsername(message.getSender().getUsername());
        resp.setContent(message.getContent());
        resp.setMessageType(message.getMessageType());
        resp.setReplyToId(message.getReplyTo() != null ? message.getReplyTo().getId() : null);
        resp.setSentAt(message.getSentAt());
        if (message.getAttachments() != null) {
            List<AttachmentDto> attachments = message.getAttachments().stream().map(a -> {
                AttachmentDto dto = new AttachmentDto();
                dto.setId(a.getId());
                dto.setFilePath(a.getFilePath());
                dto.setFileType(a.getFileType());
                dto.setFileSize(a.getFileSize());
                return dto;
            }).toList();
            resp.setAttachments(attachments);
        }
        return resp;
    }

}