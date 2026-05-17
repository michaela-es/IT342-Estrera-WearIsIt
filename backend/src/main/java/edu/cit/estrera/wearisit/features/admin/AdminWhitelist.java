package edu.cit.estrera.wearisit.features.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "admin_whitelist")
@Getter
@Setter
@NoArgsConstructor
public class AdminWhitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
}