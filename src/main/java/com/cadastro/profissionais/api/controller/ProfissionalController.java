package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.repositorie.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @GetMapping
    public List<Profissional> getProfissionais(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> fields) {

        if (q != null && !q.isEmpty()) {
            return profissionalRepository.findByAtivoTrueAndNomeContainingIgnoreCaseOrEspecialidadeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q, q);
        } else {
            return profissionalRepository.findAll()
                    .stream()
                    .filter(Profissional::getAtivo)
                    .toList();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> getProfissionalById(@PathVariable Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createProfissional(@RequestBody Profissional profissional) {
        Profissional novoProfissional = profissionalRepository.save(profissional);
        return ResponseEntity.ok("Sucesso, profissional com id " + novoProfissional.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfissional(@PathVariable Long id, @RequestBody Profissional profissionalAtualizado) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setNome(profissionalAtualizado.getNome());
                    profissional.setEspecialidade(profissionalAtualizado.getEspecialidade());
                    profissional.setTelefone(profissionalAtualizado.getTelefone());
                    profissional.setEmail(profissionalAtualizado.getEmail());
                    // Atualize outros campos conforme necessário
                    profissionalRepository.save(profissional);
                    return ResponseEntity.ok("Sucesso, cadastro alterado");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfissional(@PathVariable Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setAtivo(false); // Exclusão lógica
                    profissionalRepository.save(profissional);
                    return ResponseEntity.ok("Sucesso, profissional excluído");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

