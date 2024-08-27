package com.cadastro.profissionais.api.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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

        // Mapeia os contatos para o formato desejado
        if (contatos != null && !contatos.isEmpty()) {
            List<Map<String, Object>> contatosList = contatos.stream().map(contato -> {
                Map<String, Object> contatoMap = new HashMap<>();
                contatoMap.put("id", contato.getId());
                contatoMap.put("nome", contato.getNome());
                contatoMap.put("contato", contato.getContato());
                contatoMap.put("createdDate", contato.getCreatedDate());
                return contatoMap;
            }).collect(Collectors.toList());

            map.put("contatos", contatosList);
        } else {
            map.put("contatos", Collections.emptyList());
        }

        return map;
    }
}
