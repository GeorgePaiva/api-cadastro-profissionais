package com.cadastro.profissionais.api.domain.dto;

import com.cadastro.profissionais.api.domain.Contato;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProfissionalRequestDTO {
    private String nome;
    private String cargo;
    private Date nascimento;
    private List<Contato> contatos;
}