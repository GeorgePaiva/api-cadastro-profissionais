package com.cadastro.profissionais.api.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class ProfissionalDTO {
    private Long id;
    private String nome;
    private String cargo;
    private Date nascimento;
    private Date createdDate;
    private Boolean ativo;
}
