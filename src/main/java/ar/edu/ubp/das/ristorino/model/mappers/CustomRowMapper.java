package ar.edu.ubp.das.ristorino.model.mappers;

import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.RestConfig;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.SoapConfig;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ar.edu.ubp.das.ristorino.utils.Constantes.REST;
import static ar.edu.ubp.das.ristorino.utils.Constantes.SOAP;

// Mapea la fila de get_configuracion_restaurante a RestConfig o SoapConfig según tipo_servicio
public class CustomRowMapper<T extends RestauranteConfig> implements RowMapper<T> {

    private final Class<T> mappedClass;

    public CustomRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        String tipoServicio = rs.getString("tipo_servicio");

        if (SOAP.equalsIgnoreCase(tipoServicio) && RestauranteConfig.class.isAssignableFrom(mappedClass)) {
            SoapConfig config = new SoapConfig();
            mapCommonFields(rs, config);
            config.setNamespace(rs.getString("namespace"));
            config.setServiceName(rs.getString("service_name"));
            config.setPortName(rs.getString("port_name"));
            return mappedClass.cast(config);
        } else if (REST.equalsIgnoreCase(tipoServicio) && RestauranteConfig.class.isAssignableFrom(mappedClass)) {
            RestConfig config = new RestConfig();
            mapCommonFields(rs, config);
            config.setToken(rs.getString("token"));
            return mappedClass.cast(config);
        } else {
            throw new IllegalArgumentException("Tipo de servicio no soportado o clase mapeada inválida: " + tipoServicio);
        }
    }

    private void mapCommonFields(ResultSet rs, RestauranteConfig config) throws SQLException {
        config.setNroRestaurante(rs.getInt("nro_restaurante"));
        config.setUrlServicio(rs.getString("url_servicio"));
        config.setTipoServicio(rs.getString("tipo_servicio"));
        config.setUsername(rs.getString("username"));
        config.setPassword(rs.getString("password"));
    }
}
