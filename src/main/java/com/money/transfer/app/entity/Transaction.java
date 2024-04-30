package com.money.transfer.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a financial transaction record in the application's database
 * as requested in the README.
 */
@Getter
@ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction", schema = "public")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Reference to the account from which money are withdrawn.
     * One-to-one association, linking to the {@link Account} entity representing the source account.
     */
    @OneToOne
    private Account sourceAccount;

    /**
     * Reference to the account to which money are deposited.
     * One-to-one association, linking to the {@link Account} entity representing the target account.
     */
    @OneToOne
    private Account targetAccount;

    private float amount;

    private String currency;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;
}

