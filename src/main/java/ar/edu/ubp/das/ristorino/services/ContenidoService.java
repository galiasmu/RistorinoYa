package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.ClickRequest;
import ar.edu.ubp.das.ristorino.beans.Contenido;

import java.util.List;

public interface ContenidoService {

    // Req 12
    List<Contenido> getContenidosVigentes(String codIdioma, Integer nroRestaurante);

    // Req 50: registra el clic en el portal (lo notifica después notificarClicks)
    Integer registrarClick(ClickRequest click, Integer nroCliente);

    // Req 46: trae los contenidos a publicar de un restaurante; devuelve cuántos se guardaron
    int actualizarContenidos(int nroRestaurante);

    // Req 50: informa al restaurante los clics pendientes; devuelve cuántos se notificaron
    int notificarClicks(int nroRestaurante);

    // Req 34/48: genera con IA el texto de los contenidos que todavía no lo tienen
    int generarContenidosPromocionales();
}
