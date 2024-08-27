package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.repositorie.ContatoRepository;
import com.cadastro.profissionais.api.repositorie.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getProfissionais(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "fields", required = false) List<String> fields) {

        // Busca todos os profissinais, com ou sem filtro
        List<Profissional> profissionais;
        if (q != null && !q.isEmpty()) {
            profissionais = profissionalRepository.findByQuery(q);
        } else {
            profissionais = profissionalRepository.findAll();
        }

        // Se 'fields' for especificado, filtra os campos retornados
        List<Map<String, Object>> result = profissionais.stream()
                .map(profissional -> filterFields(profissional, fields))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> filterFields(Profissional profissional, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return profissional.toMap();
        }

        return fields.stream()
                .filter(field -> ReflectionUtils.findField(Profissional.class, field) != null)
                .collect(Collectors.toMap(field -> field, field -> {
                    Field f = ReflectionUtils.findField(Profissional.class, field);
                    ReflectionUtils.makeAccessible(f);
                    return ReflectionUtils.getField(f, profissional);
                }));
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

        List<Profissional> profissionalDB = profissionalRepository.findProfissionalByNomeAndCargoAndNascimento(
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

