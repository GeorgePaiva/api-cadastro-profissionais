package com.cadastro.profissionais.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContatoRequestDTO {
    private Long id;

    @NotBlank(message = "Nome do contato é obrigatório")
    @Size(max = 255, message = "Nome do contato deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "Contato é obrigatório")
    @Size(max = 255, message = "Contato deve ter no máximo 255 caracteres")
    private String contato;

    private Date createdDate;

    @Valid
    private ProfissionalDTO profissional;
}
