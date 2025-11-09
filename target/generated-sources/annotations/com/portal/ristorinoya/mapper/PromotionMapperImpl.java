package com.portal.ristorinoya.mapper;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.entity.Promotion;
import com.portal.ristorinoya.entity.Restaurant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-09T16:08:48-0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class PromotionMapperImpl implements PromotionMapper {

    @Override
    public PromotionDTO toDto(Promotion entity) {
        if ( entity == null ) {
            return null;
        }

        PromotionDTO.PromotionDTOBuilder promotionDTO = PromotionDTO.builder();

        promotionDTO.restaurantId( entityRestaurantId( entity ) );
        promotionDTO.active( entity.isActive() );
        promotionDTO.id( entity.getId() );
        promotionDTO.title( entity.getTitle() );
        promotionDTO.description( entity.getDescription() );
        promotionDTO.imageUrl( entity.getImageUrl() );
        promotionDTO.startAt( entity.getStartAt() );
        promotionDTO.endAt( entity.getEndAt() );
        promotionDTO.priority( entity.getPriority() );

        return promotionDTO.build();
    }

    private Long entityRestaurantId(Promotion promotion) {
        Restaurant restaurant = promotion.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        return restaurant.getId();
    }
}
