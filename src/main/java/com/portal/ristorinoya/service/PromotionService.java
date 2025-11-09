package com.portal.ristorinoya.service;

import com.portal.ristorinoya.dto.PromotionDTO;

import java.time.LocalDate;
import java.util.List;

public interface PromotionService {

    List<PromotionDTO> getContenidos(LocalDate desde, LocalDate hasta);
}