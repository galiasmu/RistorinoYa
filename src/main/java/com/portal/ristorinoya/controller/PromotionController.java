package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.service.PromotionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<PromotionDTO>> getContenidosVigentes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(promotionService.getContenidos(desde, hasta));
    }

    @GetMapping("/{nroRestaurante}/{nroIdioma}/{nroContenido}")
    public ResponseEntity<PromotionDTO> getPromotionById(
            @PathVariable int nroRestaurante,
            @PathVariable int nroIdioma,
            @PathVariable int nroContenido
    ) {
        PromotionDTO promotion = promotionService.getPromotionById(nroRestaurante, nroIdioma, nroContenido);
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(promotion);
    }
}
