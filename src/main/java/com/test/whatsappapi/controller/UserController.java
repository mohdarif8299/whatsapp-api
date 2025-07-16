package com.test.whatsappapi.controller;

import com.test.whatsappapi.dto.UserDto;
import com.test.whatsappapi.dto.UserRegisterRequest;
import com.test.whatsappapi.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegisterRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @GetMapping
    public Page<UserDto> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        return userService.list(page, size);
    }
}
