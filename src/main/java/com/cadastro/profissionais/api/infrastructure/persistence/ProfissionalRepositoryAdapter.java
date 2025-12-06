package com.cadastro.profissionais.api.infrastructure.persistence;

import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.infrastructure.persistence.jpa.ProfissionalJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ProfissionalRepositoryAdapter implements ProfissionalRepositoryPort {

    private final ProfissionalJpaRepository profissionalJpaRepository;

    public ProfissionalRepositoryAdapter(ProfissionalJpaRepository profissionalJpaRepository) {
        this.profissionalJpaRepository = profissionalJpaRepository;
    }

    @Override
    public List<Profissional> findByQuery(String q) {
        return profissionalJpaRepository.findByQuery(q);
    }

    @Override
    public List<Profissional> findAll() {
        return profissionalJpaRepository.findAll();
    }

    @Override
    public Optional<Profissional> findById(Long id) {
        return profissionalJpaRepository.findById(id);
    }

    @Override
    public List<Profissional> findByNomeCargoNascimento(String nome, String cargo, Date nascimento) {
        return profissionalJpaRepository.findProfissionalByNomeAndCargoAndNascimento(nome, cargo, nascimento);
    }

    @Override
    public Profissional save(Profissional profissional) {
        return profissionalJpaRepository.save(profissional);
    }

    @Override
    public Profissional findByNome(String nome) {
        return profissionalJpaRepository.findByNome(nome);
    }
}
