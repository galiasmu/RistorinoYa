package ar.edu.ubp.das.ristorino.repository;

import ar.edu.ubp.das.ristorino.beans.ClickPendiente;
import ar.edu.ubp.das.ristorino.beans.Contenido;
import ar.edu.ubp.das.ristorino.beans.ContenidoSinGenerar;
import ar.edu.ubp.das.ristorino.beans.restaurante.ContenidoExterno;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static ar.edu.ubp.das.ristorino.utils.Constantes.DIAS_VIGENCIA_CONTENIDO;

@Repository
public class ContenidoRepository {

    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    public List<Contenido> getContenidosVigentes(String codIdioma, Integer nroRestaurante) {
        String procedureName = "get_contenidos_vigentes";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_idioma", codIdioma)
                .addValue("nro_restaurante", nroRestaurante);

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Contenido.class);
    }

    // Req 46: alta o modificación de un contenido recibido del restaurante
    public void insUpdContenido(int nroRestaurante, ContenidoExterno contenido) {
        String procedureName = "ins_upd_contenido_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("cod_contenido_restaurante", String.valueOf(contenido.getNroContenido()))
                .addValue("nro_sucursal", contenido.getNroSucursal())
                .addValue("contenido_a_publicar", contenido.getContenidoAPublicar())
                .addValue("imagen_a_publicar", contenido.getImagenAPublicar())
                .addValue("costo_click", contenido.getCostoClick())
                .addValue("dias_vigencia", DIAS_VIGENCIA_CONTENIDO);

        simpleJdbcCallFactory.execute(procedureName, schemaName, params);
    }

    public List<ContenidoSinGenerar> getContenidosSinGenerar() {
        String procedureName = "get_contenidos_sin_generar";
        String schemaName = "dbo";

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                "resultSet",
                ContenidoSinGenerar.class);
    }

    public void updContenidoPromocional(ContenidoSinGenerar contenido, String contenidoPromocional) {
        String procedureName = "upd_contenido_promocional";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", contenido.getNroRestaurante())
                .addValue("nro_idioma", contenido.getNroIdioma())
                .addValue("nro_contenido", contenido.getNroContenido())
                .addValue("contenido_promocional", contenidoPromocional);

        simpleJdbcCallFactory.execute(procedureName, schemaName, params);
    }

    public Integer insClick(int nroRestaurante, int nroIdioma, int nroContenido, Integer nroCliente) {
        String procedureName = "ins_click_contenido";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_idioma", nroIdioma)
                .addValue("nro_contenido", nroContenido)
                .addValue("nro_cliente", nroCliente);

        List<Map> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                new ColumnMapRowMapper());

        return result.isEmpty() ? null : (Integer) result.get(0).get("nro_click");
    }

    public List<ClickPendiente> getClicksPendientes(int nroRestaurante) {
        String procedureName = "get_clicks_pendientes";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante);

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                ClickPendiente.class);
    }

    public void marcarClickNotificado(ClickPendiente click) {
        String procedureName = "upd_click_notificado";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", click.getNroRestaurante())
                .addValue("nro_idioma", click.getNroIdioma())
                .addValue("nro_contenido", click.getNroContenido())
                .addValue("nro_click", click.getNroClick());

        simpleJdbcCallFactory.execute(procedureName, schemaName, params);
    }
}
