package ar.edu.ubp.das.ristorino.repository;

import ar.edu.ubp.das.ristorino.beans.Cliente;
import ar.edu.ubp.das.ristorino.beans.RegistroRequest;
import ar.edu.ubp.das.ristorino.beans.RegistroResponse;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClienteRepository {

    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    private final Gson gson = new Gson();

    // claveHash: la contraseña ya hasheada con BCrypt
    public RegistroResponse insCliente(RegistroRequest registro, String claveHash) {
        String procedureName = "ins_cliente";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("apellido", registro.getApellido())
                .addValue("nombre", registro.getNombre())
                .addValue("clave", claveHash)
                .addValue("correo", registro.getCorreo())
                .addValue("telefonos", registro.getTelefonos())
                .addValue("nro_localidad", registro.getNroLocalidad())
                .addValue("preferencias", registro.getPreferencias() == null ? null : gson.toJson(registro.getPreferencias()));

        List<RegistroResponse> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                RegistroResponse.class);

        return result.get(0);
    }

    public Cliente findByCorreo(String correo) {
        String procedureName = "get_cliente_por_correo";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("correo", correo);

        List<Cliente> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Cliente.class);

        return result.isEmpty() ? null : result.get(0);
    }

    public Cliente getCliente(int nroCliente) {
        String procedureName = "get_cliente";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente);

        List<Cliente> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Cliente.class);

        return result.isEmpty() ? null : result.get(0);
    }
}
