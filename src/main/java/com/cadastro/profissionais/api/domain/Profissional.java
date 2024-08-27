package com.cadastro.profissionais.api.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nome", nome);
        map.put("cargo", cargo);
        map.put("nascimento", nascimento);
//        map.put("contatos", contatos != null ? contatos.get(0).getNome() : null);
        return map;
    }
}
