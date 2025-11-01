package com.portal.ristorinoya.service;

import com.portal.ristorinoya.dto.PromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    Page<PromotionDTO> getActive(Pageable pageable);
}
