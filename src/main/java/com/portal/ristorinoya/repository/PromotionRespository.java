package com.portal.ristorinoya.repository;

import com.portal.ristorinoya.beans.PromotionBean;
import com.portal.ristorinoya.components.SimpleJdbcCallFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PromotionRespository {

    private final SimpleJdbcCallFactory jdbcCallFactory;

    public PromotionRespository(SimpleJdbcCallFactory jdbcCallFactory) {
        this.jdbcCallFactory = jdbcCallFactory;
    }

    public List<PromotionBean> getContenidosVigentes(LocalDate desde, LocalDate hasta) {
        LocalDate fromDate = desde != null ? desde : LocalDate.now();
        LocalDate toDate = hasta != null ? hasta : LocalDate.now();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("FechaDesde", fromDate)
                .addValue("FechaHasta", toDate);

        return jdbcCallFactory.executeQuery(
                "sp_get_contenidos_vigentes",
                "dbo",
                params,
                "promotions",
                PromotionBean.class
        );
    }

    public PromotionBean getPromotionById(int nroRestaurante, int nroIdioma, int nroContenido) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nroRestaurante", nroRestaurante)
                .addValue("nroIdioma", nroIdioma)
                .addValue("nroContenido", nroContenido);

        List<PromotionBean> results = jdbcCallFactory.executeQuery(
                "sp_get_contenido_by_id",
                "dbo",
                params,
                "promotion",
                PromotionBean.class
        );

        return results.isEmpty() ? null : results.get(0);
    }
}
