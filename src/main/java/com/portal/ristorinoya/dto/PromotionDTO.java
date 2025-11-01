package com.portal.ristorinoya.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class PromotionDTO {
    private Long id;
    private Long restaurantId;
    private String title;
    private String description;
    private String imageUrl;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private boolean active;
    private int priority;
}
