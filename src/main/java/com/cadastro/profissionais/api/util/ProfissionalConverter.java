package com.cadastro.profissionais.api.util;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfissionalConverter {

    public static ProfissionalDTO convertToDto(Profissional profissional) {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setId(profissional.getId());
        dto.setNome(profissional.getNome());
        dto.setCargo(profissional.getCargo());
        dto.setNascimento(profissional.getNascimento());
        dto.setCreatedDate(profissional.getCreatedDate());
        dto.setAtivo(profissional.getAtivo());
        return dto;
    }

    public static ProfissionalResponseDTO convertProfissionalToDto(Profissional profissional) {
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

    public Profissional convertToEntity(ProfissionalRequestDTO dto) {
        Profissional profissional = new Profissional();
        profissional.setNome(dto.getNome());
        profissional.setCargo(dto.getCargo());
        profissional.setNascimento(dto.getNascimento());
        profissional.setAtivo(true);
        profissional.setContatos(dto.getContatos());

        return profissional;
    }

}