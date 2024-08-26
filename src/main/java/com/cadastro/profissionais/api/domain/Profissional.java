package com.cadastro.profissionais.api.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profissionais")
public class Profissional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String especialidade;
    private String telefone;
    private String email;

    private Boolean ativo = true; // Para exclusão lógica
}
