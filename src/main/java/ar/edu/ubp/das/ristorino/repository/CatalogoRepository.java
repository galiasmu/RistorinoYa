package ar.edu.ubp.das.ristorino.repository;

import ar.edu.ubp.das.ristorino.beans.EstadoReserva;
import ar.edu.ubp.das.ristorino.beans.Localidad;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CatalogoRepository {
    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    public List<ValorPreferencia> getPreferencias(String codIdioma) {
        String procedureName = "get_preferencias";
        String schemaName = "dbo";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_idioma", codIdioma);

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                ValorPreferencia.class);
    }

    public List<Localidad> getLocalidades() {
        String procedureName = "get_localidades";
        String schemaName = "dbo";

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                "resultSet",
                Localidad.class);
    }

    public List<EstadoReserva> getEstadosReservas(String codIdioma) {
        String procedureName = "get_estados_reservas";
        String schemaName = "dbo";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_idioma", codIdioma);

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                EstadoReserva.class);
    }
}
