package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.repositorie.ContatoRepository;
import com.cadastro.profissionais.api.repositorie.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @GetMapping
    public List<Profissional> getProfissionais(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> fields) {

        if (q != null && !q.isEmpty()) {
            return profissionalRepository.findByNomeContainingIgnoreCaseOrCargoContainingIgnoreCase(q, q);
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

        List<Profissional> profissionalDB = profissionalRepository.findByNomeContainingIgnoreCaseAndCargoContainingIgnoreCaseAndNascimento(
                profissional.getNome(), profissional.getCargo(), profissional.getNascimento());

        if (profissionalDB.isEmpty()) {
            profissional.setCreatedDate(new Date());
            Profissional novoProfissional = profissionalRepository.save(profissional);
            for (Contato contato : profissional.getContatos()) {
                contato.setCreatedDate(new Date());
                contato.setProfissional(profissional);
                contatoRepository.save(contato);
            }
            return ResponseEntity.ok("Sucesso, profissional com id " + novoProfissional.getId() + " cadastrado");
        } else {
            return ResponseEntity.ok("Profissional já está cadastrado.");
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfissional(@PathVariable Long id, @RequestBody Profissional profissionalAtualizado) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setNome(profissionalAtualizado.getNome());
                    profissional.setCargo(profissionalAtualizado.getCargo());
                    profissional.setNascimento(profissionalAtualizado.getNascimento());
                    profissional.setCreatedDate(profissionalAtualizado.getCreatedDate());
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

