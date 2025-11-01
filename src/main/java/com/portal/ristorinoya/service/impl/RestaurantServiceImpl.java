package com.portal.ristorinoya.service.impl;


import com.portal.ristorinoya.dto.RestaurantDTO;
import com.portal.ristorinoya.mapper.RestaurantMapper;
import com.portal.ristorinoya.repository.RestaurantRepository;
import com.portal.ristorinoya.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository repo;
    private final RestaurantMapper mapper;

    public RestaurantDTO getById(Long id) {
        var entity = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        return mapper.toDto(entity);
    }
}
