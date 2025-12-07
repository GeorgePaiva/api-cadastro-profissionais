package com.cadastro.profissionais.api.application.converter;

import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ProfissionalDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfissionalConverter {

    public ProfissionalDTO toDto(Profissional profissional) {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setId(profissional.getId());
        dto.setNome(profissional.getNome());
        dto.setCargo(profissional.getCargo());
        dto.setNascimento(profissional.getNascimento());
        dto.setCreatedDate(profissional.getCreatedDate());
        dto.setAtivo(profissional.getAtivo());
        return dto;
    }

    public ProfissionalResponseDTO toResponseDto(Profissional profissional) {
        ProfissionalResponseDTO dto = new ProfissionalResponseDTO();
        dto.setId(profissional.getId());
        dto.setNome(profissional.getNome());
        dto.setCargo(profissional.getCargo());
        dto.setNascimento(profissional.getNascimento());
        dto.setAtivo(profissional.getAtivo());
        dto.setCreatedDate(profissional.getCreatedDate());
        dto.setContatos(profissional.getContatos());
        return dto;
    }

    public Profissional toEntity(ProfissionalRequestDTO dto) {
        Profissional profissional = new Profissional();
        profissional.setNome(dto.getNome());
        profissional.setCargo(dto.getCargo());
        profissional.setNascimento(dto.getNascimento());
        profissional.setAtivo(true);
        return profissional;
    }

    public void updateEntity(Profissional profissional, ProfissionalRequestDTO dto) {
        profissional.setNome(dto.getNome());
        profissional.setCargo(dto.getCargo());
        profissional.setNascimento(dto.getNascimento());
    }
}
