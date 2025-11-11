package com.portal.ristorinoya.mapper;

import com.portal.ristorinoya.dto.PromotionDTO;
import com.portal.ristorinoya.entity.Promotion;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-10T18:33:05-0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class PromotionMapperImpl implements PromotionMapper {

    @Override
    public PromotionDTO toDto(Promotion entity) {
        if ( entity == null ) {
            return null;
        }

        Integer nroRestaurante = null;
        Integer nroIdioma = null;
        Integer nroContenido = null;
        Integer nroSucursal = null;
        String contenidoPromocional = null;
        String imagenPromocional = null;
        String contenidoAPublicar = null;
        LocalDate fechaIniVigencia = null;
        LocalDate fechaFinVigencia = null;
        Double costoClick = null;
        String codContenidoRestaurante = null;
        String razonSocial = null;
        String nomIdioma = null;
        String nomSucursal = null;

        PromotionDTO promotionDTO = new PromotionDTO( nroRestaurante, nroIdioma, nroContenido, nroSucursal, contenidoPromocional, imagenPromocional, contenidoAPublicar, fechaIniVigencia, fechaFinVigencia, costoClick, codContenidoRestaurante, razonSocial, nomIdioma, nomSucursal );

        return promotionDTO;
    }
}
