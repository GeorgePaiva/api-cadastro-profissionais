package com.cadastro.profissionais.api.repositorie;

import com.cadastro.profissionais.api.domain.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    List<Profissional> findByAtivoTrueAndNomeContainingIgnoreCaseOrEspecialidadeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nome, String especialidade, String telefone, String email);
}
