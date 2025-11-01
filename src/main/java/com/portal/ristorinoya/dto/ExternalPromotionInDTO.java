package com.portal.ristorinoya.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter @Setter
public class ExternalPromotionInDTO {
    private String restaurantName;   // obligatorio para identificar/crear el restaurante
    private String title;
    private String description;
    private String imageUrl;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private boolean active = true;
    private int priority = 0;
}
