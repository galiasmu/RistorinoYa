package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.batch.ClickBatchTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class ClickBatchController {

    private final ClickBatchTask clickBatchTask;

    /**
     * Dispara el batch que llama al SP y actualiza notificado=1
     */
    @PostMapping("/notificar-clicks")
    public ResponseEntity<BatchResponse> ejecutarBatchNotificacion() {
        int procesados = clickBatchTask.ejecutar();
        return ResponseEntity.ok(new BatchResponse("Batch ejecutado correctamente", procesados));
    }

    public record BatchResponse(String mensaje, int registrosProcesados) {}
}