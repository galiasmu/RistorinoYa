package com.portal.ristorinoya.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickBatchTask {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Ejecuta el SP dbo.sp_NotificarClicksPendientes y devuelve la cantidad actualizada.
     */
    public int ejecutar() {
        log.info("Ejecutando SP dbo.sp_NotificarClicksPendientes ...");

        Integer updated = jdbcTemplate.queryForObject(
                "EXEC dbo.sp_NotificarClicksPendientes",
                Integer.class
        );

        int cantidad = (updated != null) ? updated : 0;
        log.info("Batch finalizado. Registros actualizados: {}", cantidad);
        return cantidad;
    }
}