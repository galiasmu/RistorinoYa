package com.portal.ristorinoya.mapper;

import com.portal.ristorinoya.dto.RestaurantDTO;
import com.portal.ristorinoya.entity.Restaurant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-09T16:08:48-0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public RestaurantDTO toDto(Restaurant entity) {
        if ( entity == null ) {
            return null;
        }

        RestaurantDTO.RestaurantDTOBuilder restaurantDTO = RestaurantDTO.builder();

        restaurantDTO.id( entity.getId() );
        restaurantDTO.name( entity.getName() );
        restaurantDTO.description( entity.getDescription() );
        restaurantDTO.address( entity.getAddress() );
        restaurantDTO.phone( entity.getPhone() );
        restaurantDTO.imageUrl( entity.getImageUrl() );
        restaurantDTO.website( entity.getWebsite() );

        return restaurantDTO.build();
    }
}
