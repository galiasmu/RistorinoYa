package ar.edu.ubp.das.ristorino.repository;

import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.ResultadoBusqueda;
import ar.edu.ubp.das.ristorino.beans.Sucursal;
import ar.edu.ubp.das.ristorino.beans.restaurante.SucursalExterna;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import ar.edu.ubp.das.ristorino.model.mappers.CustomRowMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RestauranteRepository {

    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    private final Gson gson = new Gson();

    public List<Restaurante> getRestaurantes() {
        String procedureName = "get_restaurantes";
        String schemaName = "dbo";

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                "resultSet",
                Restaurante.class);
    }

    public Restaurante getRestaurante(int nroRestaurante) {
        String procedureName = "get_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante);

        List<Restaurante> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Restaurante.class);

        return result.isEmpty() ? null : result.get(0);
    }

    public Optional<RestauranteConfig> findRestauranteConfig(int nroRestaurante) {
        String procedureName = "get_configuracion_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante);

        // Ejecuta el procedimiento almacenado y mapea a RestConfig o SoapConfig según tipo_servicio
        List<RestauranteConfig> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                new CustomRowMapper<>(RestauranteConfig.class)
        );

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Sucursal> getSucursales(int nroRestaurante, String codIdioma) {
        String procedureName = "get_sucursales_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("cod_idioma", codIdioma);

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Sucursal.class);
    }

    public List<ResultadoBusqueda> buscarRestaurantes(FiltrosBusqueda filtros) {
        String procedureName = "buscar_restaurantes";
        String schemaName = "dbo";

        // Los filtros viajan al SP como JSON y se leen con OPENJSON
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("valores_dominio", filtros.getValoresDominio() == null ? null : gson.toJson(filtros.getValoresDominio()))
                .addValue("barrio", filtros.getBarrio());

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                ResultadoBusqueda.class);
    }

    // Req 45: alta o modificación de una sucursal recibida del restaurante
    public void insUpdSucursal(int nroRestaurante, SucursalExterna sucursal) {
        String procedureName = "ins_upd_sucursal_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_sucursal", sucursal.getNroSucursal())
                .addValue("nom_sucursal", sucursal.getNomSucursal())
                .addValue("calle", sucursal.getCalle())
                .addValue("nro_calle", sucursal.getNroCalle())
                .addValue("barrio", sucursal.getBarrio())
                .addValue("nom_localidad", sucursal.getNomLocalidad())
                .addValue("cod_postal", sucursal.getCodPostal())
                .addValue("telefonos", sucursal.getTelefonos())
                .addValue("total_comensales", sucursal.getTotalComensales())
                .addValue("min_tolerencia_reserva", sucursal.getMinTolerenciaReserva())
                .addValue("nom_categoria", sucursal.getNomCategoria())
                .addValue("turnos", sucursal.getTurnos())
                .addValue("zonas", sucursal.getZonas())
                .addValue("tipos_comidas", sucursal.getTiposComidas())
                .addValue("especialidades", sucursal.getEspecialidades())
                .addValue("estilos", sucursal.getEstilos());

        simpleJdbcCallFactory.execute(procedureName, schemaName, params);
    }
}
