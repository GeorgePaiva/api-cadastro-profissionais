package com.cadastro.profissionais.api.infrastructure.persistence;

import com.cadastro.profissionais.api.application.port.out.ContatoRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.infrastructure.persistence.jpa.ContatoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContatoRepositoryAdapter implements ContatoRepositoryPort {

    private final ContatoJpaRepository contatoJpaRepository;

    public ContatoRepositoryAdapter(ContatoJpaRepository contatoJpaRepository) {
        this.contatoJpaRepository = contatoJpaRepository;
    }

    @Override
    public List<Contato> findByQuery(String q) {
        return contatoJpaRepository.findByQuery(q);
    }

    @Override
    public List<Contato> findAll() {
        return contatoJpaRepository.findAll();
    }

    @Override
    public Optional<Contato> findById(Long id) {
        return contatoJpaRepository.findById(id);
    }

    @Override
    public List<Contato> findByNomeContatoProfissional(String nome, String contato, Profissional profissional) {
        return contatoJpaRepository.findContatoByNomeAndContatoAndProfissional(nome, contato, profissional);
    }

    @Override
    public Contato save(Contato contato) {
        return contatoJpaRepository.save(contato);
    }

    @Override
    public void delete(Contato contato) {
        contatoJpaRepository.delete(contato);
    }
}
