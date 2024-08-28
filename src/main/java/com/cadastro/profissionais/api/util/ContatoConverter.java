package com.cadastro.profissionais.api.util;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ContatoConverter {

    public ContatoResponseDTO convertToDto(Contato contato) {
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(contato.getId());
        dto.setNome(contato.getNome());
        dto.setContato(contato.getContato());
        dto.setCreatedDate(contato.getCreatedDate());

        if (contato.getProfissional() != null) {
            dto.setProfissional(ProfissionalConverter.convertToDto(contato.getProfissional()));
        }

        return dto;
    }

    public Contato convertToEntity(ContatoRequestDTO dto, Profissional profissional) {
        Contato contato = new Contato();
        contato.setNome(dto.getNome());
        contato.setContato(dto.getContato());
        contato.setProfissional(profissional);
        return contato;
    }

    public void updateEntity(Contato contato, ContatoRequestDTO dto) {
        contato.setNome(dto.getNome());
        contato.setContato(dto.getContato());
    }
}