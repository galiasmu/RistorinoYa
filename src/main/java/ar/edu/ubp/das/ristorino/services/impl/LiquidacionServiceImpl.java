package ar.edu.ubp.das.ristorino.services.impl;


import ar.edu.ubp.das.ristorino.beans.Liquidacion;
import ar.edu.ubp.das.ristorino.beans.LiquidacionCriteria;
import ar.edu.ubp.das.ristorino.repository.LiquidacionRepository;
import ar.edu.ubp.das.ristorino.services.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiquidacionServiceImpl implements LiquidacionService {
    @Autowired
    private LiquidacionRepository liquidacionRepository;

    @Override
    public List<Liquidacion> getLiquidacionMensual(LiquidacionCriteria criteria) {
        return liquidacionRepository.getLiquidacionMensual(criteria);
    }
}
