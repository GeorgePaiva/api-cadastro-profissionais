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
@RequestMapping("/contatos")
public class ContatoController {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getContatos(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "fields", required = false) List<String> fields) {

        // Busca todos os contatos, com ou sem filtro
        List<Contato> contatos;
        if (q != null && !q.isEmpty()) {
            contatos = contatoRepository.findByQuery(q);
        } else {
            contatos = contatoRepository.findAll();
        }

        // Se 'fields' for especificado, filtra os campos retornados
        List<Map<String, Object>> result = contatos.stream()
                .map(contato -> filterFields(contato, fields))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> filterFields(Contato contato, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return contato.toMap();
        }

        return fields.stream()
                .filter(field -> ReflectionUtils.findField(Contato.class, field) != null)
                .collect(Collectors.toMap(field -> field, field -> {
                    Field f = ReflectionUtils.findField(Contato.class, field);
                    ReflectionUtils.makeAccessible(f);
                    return ReflectionUtils.getField(f, contato);
                }));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contato> getContatoById(@PathVariable Long id) {
        return contatoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createContato(@RequestBody Contato contato) {

        // Buscando o profissional pelo nome
        if (contato.getProfissional() != null && contato.getProfissional().getNome() != null) {
            Profissional profissional = profissionalRepository.findByNome(contato.getProfissional().getNome());
            if (profissional != null) {
                contato.setProfissional(profissional);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        }

        List<Contato> contadoDB = contatoRepository.findContatoByNomeAndContatoAndProfissional(contato.getNome(),
                contato.getContato(), contato.getProfissional());

        if (contadoDB.isEmpty()) {
            contato.setCreatedDate(new Date());
            Contato novoContato = contatoRepository.save(contato);
            return ResponseEntity.ok("Sucesso, contato com id " + novoContato.getId() + " cadastrado");
        } else {
            return ResponseEntity.ok("Contato já está cadastrado.");
        }


    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateContato(@PathVariable Long id, @RequestBody Contato contatoAtualizado) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contato.setNome(contatoAtualizado.getNome());
                    contato.setContato(contatoAtualizado.getContato());
                    contato.setCreatedDate(contatoAtualizado.getCreatedDate());
                    contato.setProfissional(contatoAtualizado.getProfissional());
                    contatoRepository.save(contato);
                    return ResponseEntity.ok("Sucesso, cadastro alterado");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContato(@PathVariable Long id) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contatoRepository.delete(contato);
                    return ResponseEntity.ok("Sucesso, contato excluído");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
