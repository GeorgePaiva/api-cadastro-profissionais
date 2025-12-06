package com.cadastro.profissionais.api.application.port.out;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;

import java.util.List;
import java.util.Optional;

public interface ContatoRepositoryPort {
    List<Contato> findByQuery(String q);

    List<Contato> findAll();

    Optional<Contato> findById(Long id);

    List<Contato> findByNomeContatoProfissional(String nome, String contato, Profissional profissional);

    Contato save(Contato contato);

    void delete(Contato contato);
}
