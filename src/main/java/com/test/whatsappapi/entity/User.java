package com.test.whatsappapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 100)
    private String displayName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password; // stored as BCrypt hash

    private String profilePicture; // file path or URL

    private String about;
}
