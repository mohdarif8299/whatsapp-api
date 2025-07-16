package com.test.whatsappapi.service;


import com.test.whatsappapi.dto.UserDto;
import com.test.whatsappapi.dto.UserRegisterRequest;
import com.test.whatsappapi.entity.User;
import com.test.whatsappapi.exception.ApiException;
import com.test.whatsappapi.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found"));
        return toDto(user);
    }

    public UserDto register(UserRegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent())
            throw new ApiException("Username already exists");
        User user = User.builder()
                .username(req.getUsername())
                .displayName(req.getDisplayName())
                .email(req.getEmail())
                .password(req.getPassword())
                .profilePicture(req.getProfilePicture())
                .about(req.getAbout())
                .build();
        return toDto(userRepository.save(user));
    }

    public UserDto update(Long id, UserDto req) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found"));
        user.setDisplayName(req.getDisplayName());
        user.setEmail(req.getEmail());
        user.setProfilePicture(req.getProfilePicture());
        user.setAbout(req.getAbout());
        return toDto(userRepository.save(user));
    }

    public Page<UserDto> list(int page, int size) {
        Page<User> pg = userRepository.findAll(PageRequest.of(page, size));
        return pg.map(this::toDto);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setAbout(user.getAbout());
        return dto;
    }
}
