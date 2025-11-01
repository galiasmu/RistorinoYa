package com.portal.ristorinoya.service.impl;


import com.portal.ristorinoya.entity.ClickEvent;
import com.portal.ristorinoya.repository.ClickEventRepository;
import com.portal.ristorinoya.repository.PromotionRepository;
import com.portal.ristorinoya.service.ClickService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service @RequiredArgsConstructor
public class ClickServiceImpl implements ClickService {
    private final PromotionRepository promoRepo;
    private final ClickEventRepository clickRepo;

    @Override
    public Long registerClick(Long promotionId) {
        var promo = promoRepo.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found"));
        var ev = ClickEvent.builder()
                .promotion(promo)
                .restaurant(promo.getRestaurant())
                .clickedAt(OffsetDateTime.now())
                .status(ClickEvent.Status.PENDING)
                .attempts(0)
                .build();
        return clickRepo.save(ev).getId();
    }
}
