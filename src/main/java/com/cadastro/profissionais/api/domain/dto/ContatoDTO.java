package com.cadastro.profissionais.api.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ContatoDTO {
    private String nome;
    private String contato;
}
