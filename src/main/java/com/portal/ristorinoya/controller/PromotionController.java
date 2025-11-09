package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.service.impl.PromotionServiceImpl;
import com.portal.ristorinoya.dto.PromotionDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {

    private final PromotionServiceImpl promotionService;

    public PromotionController(PromotionServiceImpl promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/vigentes")
    public List<PromotionDTO> getContenidosVigentes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return promotionService.getContenidos(desde, hasta);
    }
}