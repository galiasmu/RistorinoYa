package com.portal.ristorinoya.repository;

import com.portal.ristorinoya.components.SimpleJdbcCallFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Map;

@Repository
public class ClickRepository {

    private final SimpleJdbcCallFactory jdbcCallFactory;

    public ClickRepository(SimpleJdbcCallFactory jdbcCallFactory) {
        this.jdbcCallFactory = jdbcCallFactory;
    }

    public Integer registrarClick(Integer nroRestaurante,
                                  Integer nroIdioma,
                                  Integer nroContenido,
                                  Integer nroCliente) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_idioma", nroIdioma)
                .addValue("nro_contenido", nroContenido)
                .addValue("nro_cliente", nroCliente)
                .addValue("costo_click", null);

        Map<String, Object> result = jdbcCallFactory.executeStoredProcedure(
                "sp_insert_click_contenido",
                "dbo",
                params,
                new SqlParameter("nro_restaurante", Types.INTEGER),
                new SqlParameter("nro_idioma", Types.INTEGER),
                new SqlParameter("nro_contenido", Types.INTEGER),
                new SqlParameter("nro_cliente", Types.INTEGER),
                new SqlParameter("costo_click", Types.DECIMAL)
        );

        return (Integer) result.get("nro_click");
    }
}