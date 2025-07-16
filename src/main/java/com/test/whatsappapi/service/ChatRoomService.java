package com.test.whatsappapi.service;

import com.test.whatsappapi.dto.ChatRoomDto;
import com.test.whatsappapi.entity.ChatRoom;
import com.test.whatsappapi.entity.User;
import com.test.whatsappapi.exception.ApiException;
import com.test.whatsappapi.repository.ChatRoomRepository;
import com.test.whatsappapi.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public ChatRoomDto create(ChatRoomDto req) {
        if (req.getUserIds() == null || req.getUserIds().isEmpty()) {
            throw new ApiException("Chat room must include at least two users");
        }

        Set<User> users = req.getUserIds().stream()
                .map(uid -> userRepository.findById(uid)
                        .orElseThrow(() -> new ApiException("User not found with id: " + uid)))
                .collect(Collectors.toSet());

        if (req.isGroup()) {
            if (req.getName() == null || req.getName().trim().isEmpty()) {
                throw new ApiException("Group chat must have a name");
            }
            if (users.size() < 3) {
                throw new ApiException("Group chat must have at least 3 users");
            }
        } else {
            if (users.size() != 2) {
                throw new ApiException("1:1 chat must include exactly 2 users");
            }
            if (req.getName() != null && !req.getName().trim().isEmpty()) {
                throw new ApiException("1:1 chat cannot have a name");
            }
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .name(req.getName())
                .isGroup(req.isGroup())
                .users(users)
                .build();
        return toDto(chatRoomRepository.save(chatRoom));
    }

    public Page<ChatRoomDto> list(int page, int size) {
        return chatRoomRepository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    private ChatRoomDto toDto(ChatRoom room) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setGroup(room.isGroup());
        dto.setUserIds(room.getUsers().stream().map(User::getId).collect(Collectors.toSet()));
        return dto;
    }
}