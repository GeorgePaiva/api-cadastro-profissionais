package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.application.port.in.ManageProfissionalUseCase;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ManageProfissionalUseCase profissionalUseCase;

    public ProfissionalController(ManageProfissionalUseCase profissionalUseCase) {
        this.profissionalUseCase = profissionalUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> getProfissionais(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<ProfissionalResponseDTO> response = profissionalUseCase.getProfissionais(q, fields);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> getProfissionalById(@PathVariable Long id) {
        return profissionalUseCase.getProfissionalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createProfissional(@Valid @RequestBody ProfissionalRequestDTO profissionalRequest) {
        return profissionalUseCase.createProfissional(profissionalRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfissional(@PathVariable Long id, @Valid @RequestBody ProfissionalRequestDTO profissionalRequest) {
        String result = profissionalUseCase.updateProfissional(id, profissionalRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfissional(@PathVariable Long id) {
        String result = profissionalUseCase.deleteProfissional(id);
        return ResponseEntity.ok(result);
    }
}
