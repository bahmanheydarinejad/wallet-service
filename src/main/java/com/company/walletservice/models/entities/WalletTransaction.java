package com.company.walletservice.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId"),
                @Index(name = "idx_reference_id", columnList = "referenceId"),
                @Index(name = "idx_timestamp", columnList = "timestamp")
        }
)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private UUID referenceId;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public WalletTransaction(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
        if (referenceId == null) referenceId = UUID.randomUUID();
    }

}
