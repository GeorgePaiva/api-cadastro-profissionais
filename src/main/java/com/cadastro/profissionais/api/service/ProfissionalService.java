package com.cadastro.profissionais.api.service;

import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import com.cadastro.profissionais.api.repositorie.ContatoRepository;
import com.cadastro.profissionais.api.repositorie.ProfissionalRepository;
import com.cadastro.profissionais.api.util.ProfissionalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ProfissionalConverter profissionalConverter;

    public List<ProfissionalResponseDTO> getProfissionais(String q, List<String> fields) {

        List<Profissional> profissionais;

        if (q != null && !q.isEmpty()) {
            profissionais = profissionalRepository.findByQuery(q);
        } else {
            profissionais = profissionalRepository.findAll();
        }

        return profissionais.stream()
                .map(profissional -> filterAndConvertToDto(profissional, fields))
                .collect(Collectors.toList());
    }

    private ProfissionalResponseDTO filterAndConvertToDto(Profissional profissional, List<String> fields) {
        ProfissionalResponseDTO dto = profissionalConverter.convertProfissionalToDto(profissional);

        if (fields != null && !fields.isEmpty()) {
            ProfissionalResponseDTO profissionalResponseDTO = new ProfissionalResponseDTO();
            if (fields.contains("id")) {
                profissionalResponseDTO.setId(dto.getId());
            }
            if (fields.contains("nome")) {
                profissionalResponseDTO.setNome(dto.getNome());
            }
            if (fields.contains("cargo")) {
                profissionalResponseDTO.setCargo(dto.getCargo());
            }
            if (fields.contains("nascimento")) {
                profissionalResponseDTO.setNascimento(dto.getNascimento());
            }
            if (fields.contains("createdDate")) {
                profissionalResponseDTO.setCreatedDate(dto.getCreatedDate());
            }
            if (fields.contains("ativo")) {
                profissionalResponseDTO.setAtivo(dto.getAtivo());
            }
            if (fields.contains("contatos")) {
                profissionalResponseDTO.setContatos(dto.getContatos());
            }

            return profissionalResponseDTO;
        }
        return dto;
    }

    public Optional<ProfissionalResponseDTO> getProfissionalById(Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(this::convertToDto);
    }

    public ResponseEntity<String> createProfissional(ProfissionalRequestDTO profissionalRequest) {
        List<Profissional> profissionalDB = profissionalRepository.findProfissionalByNomeAndCargoAndNascimento(
                profissionalRequest.getNome(), profissionalRequest.getCargo(), profissionalRequest.getNascimento());

        if (profissionalDB.isEmpty()) {
            Profissional profissional = profissionalConverter.convertToEntity(profissionalRequest);
            profissional.setCreatedDate(new Date());
            profissionalRepository.save(profissional);
            return ResponseEntity.ok("Sucesso, profissional com id " + profissional.getId() + " cadastrado");
        } else {
            return ResponseEntity.ok("Contato já está cadastrado.");
        }

    }

    public String updateProfissional(Long id, ProfissionalRequestDTO profissionalRequest) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setNome(profissionalRequest.getNome());
                    profissional.setCargo(profissionalRequest.getCargo());
                    profissional.setNascimento(profissionalRequest.getNascimento());
                    profissional.setCreatedDate(new Date());
                    profissionalRepository.save(profissional);
                    return "Sucesso, cadastro alterado";
                })
                .orElse("Profissional não encontrado");
    }

    public String deleteProfissional(Long id) {
        return profissionalRepository.findById(id)
                .filter(Profissional::getAtivo)
                .map(profissional -> {
                    profissional.setAtivo(false); // Exclusão lógica
                    profissionalRepository.save(profissional);
                    return "Sucesso, profissional excluído";
                })
                .orElse("Profissional não encontrado");
    }

    private ProfissionalResponseDTO convertToDto(Profissional profissional) {
        ProfissionalResponseDTO profissionalResponseDTO = new ProfissionalResponseDTO();

        profissionalResponseDTO.setId(profissional.getId());
        profissionalResponseDTO.setNome(profissional.getNome());
        profissionalResponseDTO.setCargo(profissional.getCargo());
        profissionalResponseDTO.setNascimento(profissional.getNascimento());
        profissionalResponseDTO.setAtivo(profissional.getAtivo());
        profissionalResponseDTO.setContatos(profissional.getContatos());

        return profissionalResponseDTO;
    }
}