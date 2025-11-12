package com.portal.ristorinoya.service;

import com.portal.ristorinoya.batch.ClickBatchTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class ClickBatchService {

    private final ClickBatchTask clickBatchTask;

    @PostMapping("/notificar-clicks")
    public ResponseEntity<Integer> notificarClicksPendientes() {
        int registrosActualizados = clickBatchTask.ejecutar();
        return ResponseEntity.ok(registrosActualizados);
    }
}
