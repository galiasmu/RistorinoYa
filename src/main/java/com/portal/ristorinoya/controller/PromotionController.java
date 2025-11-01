package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.entity.Promotion;
import com.portal.ristorinoya.entity.Restaurant;
import com.portal.ristorinoya.mapper.PromotionMapper;
import com.portal.ristorinoya.repository.PromotionRepository;
import com.portal.ristorinoya.repository.RestaurantRepository;
import com.portal.ristorinoya.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin
public class PromotionController {

    private final PromotionService service;
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final RestaurantRepository restaurantRepository;

    @GetMapping
    public Page<PromotionDTO> getActive(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "12") int size) {
        return service.getActive(PageRequest.of(page, size));
    }

    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionDTO dto) {
        // Verificar que exista el restaurante en BD
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        var entity = new Promotion();
        entity.setRestaurant(restaurant);
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());
        entity.setActive(dto.isActive()); // o true si querés forzar activo
        entity.setPriority(dto.getPriority());

        var saved = promotionRepository.save(entity);
        var result = promotionMapper.toDto(saved);
        return ResponseEntity.status(201).body(result);
    }
}
