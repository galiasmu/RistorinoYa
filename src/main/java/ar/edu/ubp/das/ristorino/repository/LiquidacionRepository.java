package ar.edu.ubp.das.ristorino.repository;


import ar.edu.ubp.das.ristorino.beans.Liquidacion;
import ar.edu.ubp.das.ristorino.beans.LiquidacionCriteria;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LiquidacionRepository {
    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;
    public List<Liquidacion> getLiquidacionMensual(LiquidacionCriteria criteria) {
        String procedureName = "get_liquidacion_mensual";
        String schemaName = "dbo";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anio", criteria.getAnio())
                .addValue("mes", criteria.getMes());

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Liquidacion.class);
    }

}
