package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.in.ManageContatoUseCase;
import com.cadastro.profissionais.api.application.port.out.ContatoRepositoryPort;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManageContatoService implements ManageContatoUseCase {

    private final ContatoRepositoryPort contatoRepository;
    private final ProfissionalRepositoryPort profissionalRepository;
    private final ContatoConverter contatoConverter;

    public ManageContatoService(ContatoRepositoryPort contatoRepository,
                                ProfissionalRepositoryPort profissionalRepository,
                                ContatoConverter contatoConverter) {
        this.contatoRepository = contatoRepository;
        this.profissionalRepository = profissionalRepository;
        this.contatoConverter = contatoConverter;
    }

    @Override
    public List<ContatoResponseDTO> getContatos(String q, List<String> fields) {
        List<Contato> contatos = (q != null && !q.isEmpty())
                ? contatoRepository.findByQuery(q)
                : contatoRepository.findAll();

        return contatos.stream()
                .map(contato -> filterFields(contatoConverter.toDto(contato), fields))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ContatoResponseDTO> getContatoById(Long id) {
        return contatoRepository.findById(id)
                .map(contatoConverter::toDto);
    }

    @Override
    public ResponseEntity<String> createContato(ContatoRequestDTO contatoRequest) {
        Profissional profissional = findProfissional(contatoRequest);

        List<Contato> contatoDB = contatoRepository.findByNomeContatoProfissional(
                contatoRequest.getNome(), contatoRequest.getContato(), profissional);

        if (contatoDB.isEmpty()) {
            Contato contato = contatoConverter.toEntity(contatoRequest, profissional);
            contato.setCreatedDate(new Date());
            contatoRepository.save(contato);
            return ResponseEntity.ok("Sucesso, contato com id " + contato.getId() + " cadastrado");
        }

        return ResponseEntity.ok("Contato já está cadastrado.");
    }

    @Override
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

    @Override
    public ResponseEntity<String> deleteContato(Long id) {
        return contatoRepository.findById(id)
                .map(contato -> {
                    contatoRepository.delete(contato);
                    return ResponseEntity.ok("Sucesso, contato excluído");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ContatoResponseDTO filterFields(ContatoResponseDTO dto, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return dto;
        }

        ContatoResponseDTO filtered = new ContatoResponseDTO();
        if (fields.contains("id")) {
            filtered.setId(dto.getId());
        }
        if (fields.contains("nome")) {
            filtered.setNome(dto.getNome());
        }
        if (fields.contains("contato")) {
            filtered.setContato(dto.getContato());
        }
        if (fields.contains("createdDate")) {
            filtered.setCreatedDate(dto.getCreatedDate());
        }
        if (fields.contains("profissional")) {
            filtered.setProfissional(dto.getProfissional());
        }
        return filtered;
    }

    private Profissional findProfissional(ContatoRequestDTO contatoRequest) {
        if (contatoRequest.getProfissional() != null && contatoRequest.getProfissional().getNome() != null) {
            return profissionalRepository.findByNome(contatoRequest.getProfissional().getNome());
        }
        return null;
    }
}
