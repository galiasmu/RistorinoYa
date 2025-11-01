package com.portal.ristorinoya.controller;


import com.portal.ristorinoya.dto.RestaurantDTO;
import com.portal.ristorinoya.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/restaurants")
@RequiredArgsConstructor @CrossOrigin
public class RestaurantController {
    private final RestaurantService service;

    @GetMapping("/{id}")
    public RestaurantDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
