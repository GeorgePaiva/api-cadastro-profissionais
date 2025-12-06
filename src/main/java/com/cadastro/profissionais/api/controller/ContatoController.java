package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.application.port.in.ManageContatoUseCase;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contatos")
public class ContatoController {

    private final ManageContatoUseCase contatoUseCase;

    public ContatoController(ManageContatoUseCase contatoUseCase) {
        this.contatoUseCase = contatoUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponseDTO>> getContatos(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "fields", required = false) List<String> fields) {

        List<ContatoResponseDTO> contatos = contatoUseCase.getContatos(q, fields);
        return ResponseEntity.ok(contatos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponseDTO> getContatoById(@PathVariable Long id) {
        return contatoUseCase.getContatoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createContato(@RequestBody ContatoRequestDTO contatoRequest) {
        return contatoUseCase.createContato(contatoRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateContato(@PathVariable Long id, @RequestBody ContatoRequestDTO contatoRequest) {
        return contatoUseCase.updateContato(id, contatoRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContato(@PathVariable Long id) {
        return contatoUseCase.deleteContato(id);
    }
}
