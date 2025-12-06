package com.cadastro.profissionais.api.infrastructure.persistence.jpa;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContatoJpaRepository extends JpaRepository<Contato, Long> {

    @Query("SELECT c FROM Contato c WHERE c.nome LIKE %:q% OR c.contato LIKE %:q%")
    List<Contato> findByQuery(@Param("q") String q);

    @Query("SELECT c FROM Contato c WHERE c.nome LIKE %:nome% AND c.contato LIKE %:contato% AND c.profissional = :profissional")
    List<Contato> findContatoByNomeAndContatoAndProfissional(String nome, String contato, Profissional profissional);
}
