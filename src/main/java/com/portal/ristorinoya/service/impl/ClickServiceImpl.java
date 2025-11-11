package com.portal.ristorinoya.service.impl;


// package com.tuapp.ristorino.clicks.service.impl;

import com.portal.ristorinoya.service.ClickService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClickServiceImpl implements ClickService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer registrarClick(Integer nroRestaurante,
                                  Integer nroIdioma,
                                  Integer nroContenido,
                                  Integer nroCliente) {

        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo") // si tu schema es otro, cámbialo
                .withProcedureName("sp_registrar_click_contenido")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nro_restaurante", Types.INTEGER),
                        new SqlParameter("nro_idioma", Types.INTEGER),
                        new SqlParameter("nro_contenido", Types.INTEGER),
                        new SqlParameter("nro_cliente", Types.INTEGER),
                        new SqlOutParameter("nro_click", Types.INTEGER)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_idioma", nroIdioma)
                .addValue("nro_contenido", nroContenido)
                .addValue("nro_cliente", nroCliente);

        Map<String, Object> out = call.execute(params);

        return (Integer) out.get("nro_click");
    }
}
