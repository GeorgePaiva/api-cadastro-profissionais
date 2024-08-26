package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.repositorie.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contatos")
public class ContatoController {

    @Autowired
    private ContatoRepository contatoRepository;

    @GetMapping
    public List<Contato> getContatos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> fields) {

        if (q != null && !q.isEmpty()) {
            return contatoRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCase(q, q, q);
        } else {
            return contatoRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contato> getContatoById(@PathVariable Long id) {
        return contatoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createContato(@RequestBody Contato contato) {
        Contato novoContato = contatoRepository.save(contato);
        return ResponseEntity.ok("Sucesso, contato com id " + novoContato.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateContato(@PathVariable Long id, @RequestBody Contato contatoAtualizado) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contato.setNome(contatoAtualizado.getNome());
                    contato.setEmail(contatoAtualizado.getEmail());
                    contato.setTelefone(contatoAtualizado.getTelefone());
                    // Atualize outros campos conforme necessário
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

