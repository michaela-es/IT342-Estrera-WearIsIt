package edu.cit.estrera.wearisit.features.user_management;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Getter
    @Column(nullable = false)
    private String role = "USER";

    @Setter
    @Column(length = 20)
    private String provider;

    @Setter
    @Column(length = 255)
    private String providerId;

    @Column
    private LocalDateTime updated_at;


    @Column
    private LocalDateTime last_login;

    @Column(nullable = false)
    private Boolean is_active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Getter
    @Column(nullable = false)
    private Boolean enabled = true;

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Long getUser_id() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getRole(){ return role;}

    public LocalDateTime getLast_login() {
        return last_login;
    }

    public void setLast_login(LocalDateTime last_login) {
        this.last_login = last_login;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setProviderId(String providerId){
        this.providerId = providerId;
    }
    public void setProvider(String provider){
        this.provider = provider;
    }
    public boolean getEnabled() {
        return enabled;
    }

    public void setRole(String role) {
        this.role = role;
    }
}