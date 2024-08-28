package com.cadastro.profissionais.api.domain.dto;

import com.cadastro.profissionais.api.domain.Contato;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfissionalResponseDTO {
    private Long id;
    private String nome;
    private String cargo;
    private Date nascimento;
    private Date createdDate;
    private Boolean ativo;
    private List<Contato> contatos;
}