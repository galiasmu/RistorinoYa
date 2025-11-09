package com.portal.ristorinoya.service.impl;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.repository.PromotionRespository;
import com.portal.ristorinoya.service.PromotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRespository promotionRespository;

    public PromotionServiceImpl(PromotionRespository promotionRespository) {
        this.promotionRespository = promotionRespository;
    }

    public List<PromotionDTO> getContenidos(LocalDate desde, LocalDate hasta) {

        // ✅ If null, apply defaults
        if (desde == null) {
            desde = LocalDate.now();
        }

        if (hasta == null) {
            hasta = LocalDate.now().plusMonths(1);
        }

        return promotionRespository.getContenidosVigentes(desde, hasta);
    }
}