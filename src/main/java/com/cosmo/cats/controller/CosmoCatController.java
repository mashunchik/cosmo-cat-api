package com.cosmo.cats.controller;

import com.cosmo.cats.model.CosmoCat;
import com.cosmo.cats.service.CosmoCatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cosmo-cats")
public class CosmoCatController {

    private final CosmoCatService cosmoCatService;

    public CosmoCatController(CosmoCatService cosmoCatService) {
        this.cosmoCatService = cosmoCatService;
    }

    @GetMapping
    public ResponseEntity<List<CosmoCat>> getCosmoCats() {
        return ResponseEntity.ok(cosmoCatService.getCosmoCats());
    }
}
