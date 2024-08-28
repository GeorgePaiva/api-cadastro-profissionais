package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import com.cadastro.profissionais.api.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> getProfissionais(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<ProfissionalResponseDTO> response = profissionalService.getProfissionais(q, fields);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> getProfissionalById(@PathVariable Long id) {
        return profissionalService.getProfissionalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createProfissional(@RequestBody ProfissionalRequestDTO profissionalRequest) {
        return profissionalService.createProfissional(profissionalRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfissional(@PathVariable Long id, @RequestBody ProfissionalRequestDTO profissionalRequest) {
        String result = profissionalService.updateProfissional(id, profissionalRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfissional(@PathVariable Long id) {
        String result = profissionalService.deleteProfissional(id);
        return ResponseEntity.ok(result);
    }
}