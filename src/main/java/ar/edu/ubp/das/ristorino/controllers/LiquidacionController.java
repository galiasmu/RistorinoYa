package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.Liquidacion;
import ar.edu.ubp.das.ristorino.beans.LiquidacionCriteria;
import ar.edu.ubp.das.ristorino.services.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/liquidaciones")
public class LiquidacionController {
    @Autowired
    private LiquidacionService liquidacionService;

    @PostMapping
    public List<Liquidacion> getLiquidacionMensual(@RequestBody LiquidacionCriteria criteria) {
        return liquidacionService.getLiquidacionMensual(criteria);
    }

}
