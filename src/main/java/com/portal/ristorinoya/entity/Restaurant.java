package com.portal.ristorinoya.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "restaurant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=120) private String name;
    @Column(length=500) private String description;
    @Column(length=200) private String address;
    @Column(length=50)  private String phone;
    @Column(length=400) private String imageUrl;
    @Column(length=200) private String website;
}
