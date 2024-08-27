package com.cadastro.profissionais.api.repositorie;

import com.cadastro.profissionais.api.domain.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    @Query("SELECT c FROM Contato c WHERE c.nome LIKE %:q% OR c.contato LIKE %:q%")
    List<Contato> findByQuery(@Param("q") String q);

    @Query("SELECT c FROM Contato c WHERE c.nome LIKE %:nome% AND c.contato LIKE %:contato%")
    List<Contato> findContatoByNomeAndContatoAndProfissional(String nome, String contato);
}

