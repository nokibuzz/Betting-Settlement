package com.sportygroup.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bet", indexes = {
        @Index(name = "idx_event_id", columnList = "eventId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String betId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventMarketId;

    @Column(nullable = false)
    private String predictedWinnerId;

    @Column(nullable = false)
    private BigDecimal betAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BetStatus status = BetStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BetResult result = BetResult.ONGOING;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}