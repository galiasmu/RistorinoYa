package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.dto.ExternalPromotionInDTO;
import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.entity.Promotion;
import com.portal.ristorinoya.entity.Restaurant;
import com.portal.ristorinoya.mapper.PromotionMapper;
import com.portal.ristorinoya.repository.PromotionRepository;
import com.portal.ristorinoya.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
@CrossOrigin
public class ExternalIngestController {

    private final RestaurantRepository restaurantRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @PostMapping("/promotions")
    public ResponseEntity<PromotionDTO> ingestPromotion(@RequestBody ExternalPromotionInDTO dto) {
        if (dto.getRestaurantName() == null || dto.getRestaurantName().isBlank()) {
            throw new IllegalArgumentException("restaurantName is required");
        }
        // buscar o crear restaurante por nombre
        var restaurant = restaurantRepository
                .findByNameIgnoreCase(dto.getRestaurantName())
                .orElseGet(() -> {
                    var r = new Restaurant();
                    r.setName(dto.getRestaurantName());
                    return restaurantRepository.save(r);
                });

        var p = new Promotion();
        p.setRestaurant(restaurant);
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());
        p.setStartAt(dto.getStartAt());
        p.setEndAt(dto.getEndAt());
        p.setActive(dto.isActive());
        p.setPriority(dto.getPriority());

        var saved = promotionRepository.save(p);
        return ResponseEntity.status(201).body(promotionMapper.toDto(saved));
    }
}
