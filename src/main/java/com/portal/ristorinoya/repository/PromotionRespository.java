package com.portal.ristorinoya.repository;

import com.portal.ristorinoya.dto.PromotionDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PromotionRespository {

    @PersistenceContext
    private EntityManager em;

    public List<PromotionDTO> getContenidosVigentes(LocalDate desde, LocalDate hasta) {

        Query query = em.createNativeQuery(
                "EXEC sp_get_contenidos_vigentes :desde, :hasta"
        );

        query.setParameter("desde", Date.valueOf(desde));
        query.setParameter("hasta", Date.valueOf(hasta));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream().map(row ->
                new PromotionDTO(
                        (Integer) row[0],
                        (Integer) row[1],
                        (Integer) row[2],
                        (Integer) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        ((Date) row[7]).toLocalDate(),
                        ((Date) row[8]).toLocalDate(),
                        (row[9] != null ? ((Number) row[9]).doubleValue() : null),
                        (String) row[10],
                        (String) row[11],
                        (String) row[12],
                        (String) row[13]
                )
        ).collect(Collectors.toList());
    }
}