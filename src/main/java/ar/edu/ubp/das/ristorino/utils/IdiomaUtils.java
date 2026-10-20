package ar.edu.ubp.das.ristorino.utils;

public class IdiomaUtils {

    // "es-AR,es;q=0.9" → "es". El portal maneja español (es) e inglés (en).
    public static String codIdioma(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.length() < 2) {
            return Constantes.IDIOMA_POR_DEFECTO;
        }
        return acceptLanguage.substring(0, 2).toLowerCase();
    }
}
