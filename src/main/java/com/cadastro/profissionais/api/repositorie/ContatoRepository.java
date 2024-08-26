package com.cadastro.profissionais.api.repositorie;

import com.cadastro.profissionais.api.domain.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {
    List<Contato> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCase(String nome, String email, String telefone);
}

