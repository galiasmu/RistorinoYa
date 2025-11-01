package com.portal.ristorinoya.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name = "promotion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Promotion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="restaurant_id", nullable=false)
    private Restaurant restaurant;
    @Column(nullable=false, length=140) private String title;
    @Column(length=600) private String description;
    @Column(length=400) private String imageUrl;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    @Column(nullable=false) private boolean isActive = true;
    @Column(nullable=false) private int priority = 0;
}
