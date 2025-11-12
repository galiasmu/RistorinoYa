package com.portal.ristorinoya.service;

import com.portal.ristorinoya.beans.PromotionBean;
import com.portal.ristorinoya.repository.PromotionRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionService {

    @Autowired
    private PromotionRespository promotionRespository;

    @GetMapping
    public ResponseEntity<List<PromotionBean>> getContenidosVigentes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<PromotionBean> promotions = promotionRespository.getContenidosVigentes(desde, hasta);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{nroRestaurante}/{nroIdioma}/{nroContenido}")
    public ResponseEntity<PromotionBean> getPromotionById(
            @PathVariable int nroRestaurante,
            @PathVariable int nroIdioma,
            @PathVariable int nroContenido) {
        PromotionBean promotion = promotionRespository.getPromotionById(nroRestaurante, nroIdioma, nroContenido);
        return promotion != null ? ResponseEntity.ok(promotion) : ResponseEntity.notFound().build();
    }
}
