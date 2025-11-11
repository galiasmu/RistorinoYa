package com.portal.ristorinoya.mapper;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.entity.Promotion;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface PromotionMapper {
    //@Mapping(source = "restaurant.id", target = "restaurantId")
    //@Mapping(source = "active", target = "active")
    PromotionDTO toDto(Promotion entity);
}
