package ar.edu.ubp.das.ristorino.repository;

import ar.edu.ubp.das.ristorino.beans.Costo;
import ar.edu.ubp.das.ristorino.beans.ReservaCliente;
import ar.edu.ubp.das.ristorino.beans.ReservaRequest;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
public class ReservaRepository {

    @Autowired
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    public Costo getCostoVigente(String tipoCosto) {
        String procedureName = "get_costo_vigente";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("tipo_costo", tipoCosto);

        List<Costo> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                Costo.class);

        return result.isEmpty() ? null : result.get(0);
    }

    // Guarda en el portal una reserva que el restaurante ya confirmó
    public Integer insReserva(int nroCliente, ReservaRequest reserva, BigDecimal costoReserva, String codReservaSucursal) {
        String procedureName = "ins_reserva_restaurante";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente)
                .addValue("nro_restaurante", reserva.getNroRestaurante())
                .addValue("nro_sucursal", reserva.getNroSucursal())
                .addValue("cod_zona", reserva.getCodZona())
                .addValue("fecha_reserva", Date.valueOf(LocalDate.parse(reserva.getFecha())))
                .addValue("hora_reserva", Time.valueOf(LocalTime.parse(reserva.getHora())))
                .addValue("cant_adultos", reserva.getCantAdultos())
                .addValue("cant_menores", reserva.getCantMenores())
                .addValue("costo_reserva", costoReserva)
                .addValue("cod_reserva_sucursal", codReservaSucursal);

        List<Map> result = simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                new ColumnMapRowMapper());

        return result.isEmpty() ? null : (Integer) result.get(0).get("nro_reserva");
    }

    public List<ReservaCliente> getReservasCliente(int nroCliente, String codIdioma, Integer codEstado, String fechaDesde, String fechaHasta) {
        String procedureName = "get_reservas_cliente";
        String schemaName = "dbo";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_cliente", nroCliente)
                .addValue("cod_idioma", codIdioma)
                .addValue("cod_estado", codEstado)
                .addValue("fecha_desde", fechaDesde == null ? null : Date.valueOf(LocalDate.parse(fechaDesde)))
                .addValue("fecha_hasta", fechaHasta == null ? null : Date.valueOf(LocalDate.parse(fechaHasta)));

        return simpleJdbcCallFactory.executeQuery(
                procedureName,
                schemaName,
                params,
                "resultSet",
                ReservaCliente.class);
    }
}
