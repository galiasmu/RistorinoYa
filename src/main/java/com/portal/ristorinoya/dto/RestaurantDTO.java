package com.portal.ristorinoya.dto;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String imageUrl;
    private String website;
}

