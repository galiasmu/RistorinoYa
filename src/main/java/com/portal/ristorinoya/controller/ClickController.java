package com.portal.ristorinoya.controller;


import com.portal.ristorinoya.dto.ClickCreateDTO;
import com.portal.ristorinoya.service.ClickService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/clicks")
@RequiredArgsConstructor @CrossOrigin
public class ClickController {
    private final ClickService service;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ClickCreateDTO dto) {
        var id = service.registerClick(dto.getPromotionId());
        return ResponseEntity.status(201).body(id);
    }
}
