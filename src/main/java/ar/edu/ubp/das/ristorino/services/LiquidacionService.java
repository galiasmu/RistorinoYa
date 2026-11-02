package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.Liquidacion;
import ar.edu.ubp.das.ristorino.beans.LiquidacionCriteria;

import java.util.List;

public interface LiquidacionService {
    List<Liquidacion> getLiquidacionMensual(LiquidacionCriteria criteria);
}
