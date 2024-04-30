package com.money.transfer.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Represents an account entity within the application as requested in the README
 */
@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account", schema = "public")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private float balance;

    private String currency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
