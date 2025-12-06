package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ProfissionalConverter;
import com.cadastro.profissionais.api.application.port.in.ManageProfissionalUseCase;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManageProfissionalService implements ManageProfissionalUseCase {

    private final ProfissionalRepositoryPort profissionalRepository;
    private final ProfissionalConverter profissionalConverter;

    public ManageProfissionalService(ProfissionalRepositoryPort profissionalRepository,
                                     ProfissionalConverter profissionalConverter) {
        this.profissionalRepository = profissionalRepository;
        this.profissionalConverter = profissionalConverter;
    }

    @Override
    public List<ProfissionalResponseDTO> getProfissionais(String q, List<String> fields) {
        List<Profissional> profissionais = (q != null && !q.isEmpty())
                ? profissionalRepository.findByQuery(q)
                : profissionalRepository.findAll();

        return profissionais.stream()
                .filter(Profissional::getAtivo)
                .map(profissional -> filterFields(profissionalConverter.toResponseDto(profissional), fields))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProfissionalResponseDTO> getProfissionalById(Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissionalConverter::toResponseDto);
    }

    @Override
    public ResponseEntity<String> createProfissional(ProfissionalRequestDTO profissionalRequest) {
        List<Profissional> profissionalDB = profissionalRepository.findByNomeCargoNascimento(
                profissionalRequest.getNome(), profissionalRequest.getCargo(), profissionalRequest.getNascimento());

        if (profissionalDB.isEmpty()) {
            Profissional profissional = profissionalConverter.toEntity(profissionalRequest);
            profissional.setCreatedDate(new Date());
            profissionalRepository.save(profissional);
            return ResponseEntity.ok("Sucesso, profissional com id " + profissional.getId() + " cadastrado");
        }

        return ResponseEntity.ok("Contato já está cadastrado.");
    }

    @Override
    public String updateProfissional(Long id, ProfissionalRequestDTO profissionalRequest) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissionalConverter.updateEntity(profissional, profissionalRequest);
                    profissional.setCreatedDate(new Date());
                    profissionalRepository.save(profissional);
                    return "Sucesso, cadastro alterado";
                })
                .orElse("Profissional não encontrado");
    }

    @Override
    public String deleteProfissional(Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setAtivo(false);
                    profissionalRepository.save(profissional);
                    return "Sucesso, profissional excluído";
                })
                .orElse("Profissional não encontrado");
    }

    private ProfissionalResponseDTO filterFields(ProfissionalResponseDTO dto, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return dto;
        }

        ProfissionalResponseDTO filtered = new ProfissionalResponseDTO();
        if (fields.contains("id")) {
            filtered.setId(dto.getId());
        }
        if (fields.contains("nome")) {
            filtered.setNome(dto.getNome());
        }
        if (fields.contains("cargo")) {
            filtered.setCargo(dto.getCargo());
        }
        if (fields.contains("nascimento")) {
            filtered.setNascimento(dto.getNascimento());
        }
        if (fields.contains("createdDate")) {
            filtered.setCreatedDate(dto.getCreatedDate());
        }
        if (fields.contains("ativo")) {
            filtered.setAtivo(dto.getAtivo());
        }
        if (fields.contains("contatos")) {
            filtered.setContatos(dto.getContatos());
        }
        return filtered;
    }
}
