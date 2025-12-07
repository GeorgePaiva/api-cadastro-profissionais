package com.cadastro.profissionais.api.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProfissionalRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Cargo é obrigatório")
    private String cargo;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve estar no passado")
    private Date nascimento;

    @Valid
    private List<ContatoRequestDTO> contatos;
}