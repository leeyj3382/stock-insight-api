package com.leeyujun.stockinsightapi.domain.user.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String email;


    // BCrypt hash
    @Column(nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자
    protected User(){

    }

    public User(String email, String passwordHash, String nickname, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.role = (role == null) ? UserRole.USER : role;
    }


    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if(this.role == null) {
            this.role = UserRole.USER;
        }
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getNickname() { return nickname; }
    public UserRole getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // 도메인 메서드
    public void changeNickname(String nickname) { this.nickname = nickname; }

}
