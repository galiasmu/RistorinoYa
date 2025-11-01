package com.portal.ristorinoya.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name = "click_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClickEvent {
    public enum Status { PENDING, SENT, FAILED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="promotion_id", nullable=false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="restaurant_id", nullable=false)
    private Restaurant restaurant;

    @Column(nullable=false) private OffsetDateTime clickedAt;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=10)
    private Status status = Status.PENDING;

    @Column(nullable=false) private int attempts = 0;
}
