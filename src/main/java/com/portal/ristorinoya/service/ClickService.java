package com.portal.ristorinoya.service;

import com.portal.ristorinoya.repository.ClickRepository;
import com.portal.ristorinoya.service.ClickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clicks")
@RequiredArgsConstructor
public class ClickService {

    private final ClickRepository clickRepository;

    @PostMapping
    public ResponseEntity<Integer> registrarClick(
            @RequestParam Integer nroRestaurante,
            @RequestParam Integer nroIdioma,
            @RequestParam Integer nroContenido,
            @RequestParam Integer nroCliente) {
        Integer nroClick = clickRepository.registrarClick(nroRestaurante, nroIdioma, nroContenido, nroCliente);
        return ResponseEntity.ok(nroClick);
    }
}
