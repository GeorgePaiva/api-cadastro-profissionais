package com.cadastro.profissionais.api.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "profissionais")
public class Profissional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cargo;
    private Date nascimento;
    private Date createdDate;
    private Boolean ativo = true;

    @OneToMany(mappedBy = "profissional")
    private List<Contato> contatos;
}
