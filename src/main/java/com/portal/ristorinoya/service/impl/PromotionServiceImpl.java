package com.portal.ristorinoya.service.impl;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.mapper.PromotionMapper;
import com.portal.ristorinoya.repository.PromotionRepository;
import com.portal.ristorinoya.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service @RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository repo;
    private final PromotionMapper mapper;

    @Override
    public Page<PromotionDTO> getActive(Pageable pageable) {
        var now = OffsetDateTime.now();
        return repo
                .findByIsActiveTrueAndStartAtBeforeAndEndAtAfterOrderByPriorityDesc(now, now, pageable)
                .map(mapper::toDto);
    }
}
