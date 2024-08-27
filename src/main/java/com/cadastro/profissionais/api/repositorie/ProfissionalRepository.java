package com.cadastro.profissionais.api.repositorie;

import com.cadastro.profissionais.api.domain.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    @Query("SELECT p FROM Profissional p WHERE p.nome LIKE %:nome% AND p.cargo LIKE %:cargo% AND p.nascimento = :nascimento")
    List<Profissional> findProfissionalByNomeAndCargoAndNascimento(String nome, String cargo, Date nascimento);

    @Query("SELECT p FROM Profissional p WHERE p.nome LIKE %:q% OR p.cargo LIKE %:q%")
    List<Profissional> findByQuery(@Param("q") String q);

    Profissional findByNome(String nome);
}
