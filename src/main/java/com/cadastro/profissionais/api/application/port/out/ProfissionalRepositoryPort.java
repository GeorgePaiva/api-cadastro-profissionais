package com.cadastro.profissionais.api.application.port.out;

import com.cadastro.profissionais.api.domain.Profissional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProfissionalRepositoryPort {
    List<Profissional> findByQuery(String q);

    List<Profissional> findAll();

    Optional<Profissional> findById(Long id);

    List<Profissional> findByNomeCargoNascimento(String nome, String cargo, Date nascimento);

    Profissional save(Profissional profissional);

    Profissional findByNome(String nome);
}
