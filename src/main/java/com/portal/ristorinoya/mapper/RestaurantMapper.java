package com.portal.ristorinoya.mapper;

import com.portal.ristorinoya.dto.RestaurantDTO;
import com.portal.ristorinoya.entity.Restaurant;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    RestaurantDTO toDto(Restaurant entity);
}
