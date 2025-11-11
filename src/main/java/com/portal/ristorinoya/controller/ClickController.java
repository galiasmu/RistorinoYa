package com.portal.ristorinoya.controller;

import com.portal.ristorinoya.dto.ClickCreateDTO;
import com.portal.ristorinoya.service.ClickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/clicks")
@RequiredArgsConstructor
@CrossOrigin
public class ClickController {

    private final ClickService clickService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> create(@RequestBody ClickCreateDTO dto) {

        Integer nroClick = clickService.registrarClick(
                dto.getNroRestaurante(),
                dto.getNroIdioma(),
                dto.getNroContenido(),
                dto.getNroCliente() // hoy será null porque el front no lo manda
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("nroClick", nroClick));
    }
}
