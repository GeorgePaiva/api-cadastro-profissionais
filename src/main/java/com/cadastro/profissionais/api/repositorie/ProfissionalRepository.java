package com.cadastro.profissionais.api.repositorie;

import com.cadastro.profissionais.api.domain.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    List<Profissional> findByNomeContainingIgnoreCaseAndCargoContainingIgnoreCaseAndNascimento(
            String nome, String cargo, Date nascimento);
    List<Profissional> findByNomeContainingIgnoreCaseOrCargoContainingIgnoreCase(
            String nome, String cargo);
}
