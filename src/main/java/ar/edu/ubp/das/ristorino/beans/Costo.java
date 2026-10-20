package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Costo {
    private String tipoCosto;
    private BigDecimal monto;
}
