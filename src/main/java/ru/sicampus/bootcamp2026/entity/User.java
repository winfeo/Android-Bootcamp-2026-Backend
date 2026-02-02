package ru.sicampus.bootcamp2026.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "login_data_id", nullable = false, unique = true)
    private LoginData loginData;

    @Column(name = "full_name", nullable = false)
    private String fullName;

//    TODO добавить ссылку на фото
//    @Column(name = "photo_url")
//    private String photoUrl;

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
        loginData.setUser(this);
    }
}
