package com.cadastro.profissionais.api.infrastructure.persistence.jpa;

import com.cadastro.profissionais.api.domain.Profissional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProfissionalJpaRepositoryTest {

    @Autowired
    private ProfissionalJpaRepository profissionalJpaRepository;

    @Test
    void shouldFindByQueryAndByNomeCargoNascimento() {
        Profissional profissional = new Profissional();
        profissional.setNome("Maria");
        profissional.setCargo("Engenheira");
        profissional.setNascimento(new Date());
        profissional.setAtivo(true);

        profissionalJpaRepository.save(profissional);

        List<Profissional> byQuery = profissionalJpaRepository.findByQuery("Maria");
        assertThat(byQuery).hasSize(1);

        List<Profissional> byNomeCargoNascimento = profissionalJpaRepository
                .findProfissionalByNomeAndCargoAndNascimento("Maria", "Engenheira", profissional.getNascimento());
        assertThat(byNomeCargoNascimento).hasSize(1);
    }

    @Test
    void shouldFindByNome() {
        Profissional profissional = new Profissional();
        profissional.setNome("Carlos");
        profissional.setCargo("Analista");
        profissional.setNascimento(new Date());
        profissional.setAtivo(true);

        profissionalJpaRepository.save(profissional);

        Profissional found = profissionalJpaRepository.findByNome("Carlos");
        assertThat(found).isNotNull();
        assertThat(found.getCargo()).isEqualTo("Analista");
    }
}
