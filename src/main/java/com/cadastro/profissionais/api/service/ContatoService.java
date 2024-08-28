package com.cadastro.profissionais.api.service;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import com.cadastro.profissionais.api.repositorie.ContatoRepository;
import com.cadastro.profissionais.api.repositorie.ProfissionalRepository;
import com.cadastro.profissionais.api.util.ContatoConverter;
import com.cadastro.profissionais.api.util.ProfissionalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ContatoConverter contatoConverter;

    public List<ContatoResponseDTO> getContatos(String q, List<String> fields) {

        List<Contato> contatos;

        if (q != null && !q.isEmpty()) {
            contatos = contatoRepository.findByQuery(q);
        } else {
            contatos = contatoRepository.findAll();
        }

        return contatos.stream()
                .map(contato -> filterAndConvertToDto(contato, fields))
                .collect(Collectors.toList());
    }

    private ContatoResponseDTO filterAndConvertToDto(Contato contato, List<String> fields) {
        ContatoResponseDTO dto = contatoConverter.convertToDto(contato);

        if (fields != null && !fields.isEmpty()) {
            ContatoResponseDTO contatoResponseDTO = new ContatoResponseDTO();
            if (fields.contains("id")) {
                contatoResponseDTO.setId(dto.getId());
            }
            if (fields.contains("nome")) {
                contatoResponseDTO.setNome(dto.getNome());
            }
            if (fields.contains("contato")) {
                contatoResponseDTO.setContato(dto.getContato());
            }
            if (fields.contains("createdDate")) {
                contatoResponseDTO.setCreatedDate(dto.getCreatedDate());
            }
            if (fields.contains("profissional")) {
                contatoResponseDTO.setProfissional(dto.getProfissional());
            }

            return contatoResponseDTO;
        }
        return dto;
    }

    public Optional<ContatoResponseDTO> getContatoById(Long id) {
        return contatoRepository.findById(id)
                .map(contatoConverter::convertToDto);
    }

    public ResponseEntity<String> createContato(ContatoRequestDTO contatoRequest) {
        Profissional profissional = findProfissional(contatoRequest);

        List<Contato> contadoDB = contatoRepository.findContatoByNomeAndContatoAndProfissional(
                contatoRequest.getNome(), contatoRequest.getContato(), profissional);

        if (contadoDB.isEmpty()) {
            Contato contato = contatoConverter.convertToEntity(contatoRequest, profissional);
            contato.setCreatedDate(new Date());
            contatoRepository.save(contato);
            return ResponseEntity.ok("Sucesso, contato com id " + contato.getId() + " cadastrado");
        } else {
            return ResponseEntity.ok("Contato já está cadastrado.");
        }
    }

    public ResponseEntity<String> updateContato(Long id, ContatoRequestDTO contatoRequest) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contatoConverter.updateEntity(contato, contatoRequest);
                    contato.setCreatedDate(new Date());
                    contatoRepository.save(contato);
                    return ResponseEntity.ok("Sucesso, cadastro alterado");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<String> deleteContato(Long id) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contatoRepository.delete(contato);
                    return ResponseEntity.ok("Sucesso, contato excluído");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Profissional findProfissional(ContatoRequestDTO contatoRequest) {
        if (contatoRequest.getProfissional() != null && contatoRequest.getProfissional().getNome() != null) {
            return profissionalRepository.findByNome(contatoRequest.getProfissional().getNome());
        }
        return null;
    }
}
