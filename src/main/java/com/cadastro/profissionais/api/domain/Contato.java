package com.cadastro.profissionais.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "contatos")
@Getter
@Setter
public class Contato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String contato;
    private Date createdDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = true)
    private Profissional profissional;


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nome", nome);
        map.put("contato", contato);
        map.put("profissional", profissional != null ? profissional.getNome() : null);
        return map;
    }
}

