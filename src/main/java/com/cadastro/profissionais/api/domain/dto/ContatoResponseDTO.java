package com.cadastro.profissionais.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContatoResponseDTO {
    private Long id;
    private String nome;
    private String contato;
    private Date createdDate;
    private ProfissionalDTO profissional;

}