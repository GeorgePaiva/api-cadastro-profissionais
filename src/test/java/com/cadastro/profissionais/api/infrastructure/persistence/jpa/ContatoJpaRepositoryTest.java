package com.cadastro.profissionais.api.infrastructure.persistence.jpa;

import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContatoJpaRepositoryTest {

    @Autowired
    private ContatoJpaRepository contatoJpaRepository;

    @Autowired
    private ProfissionalJpaRepository profissionalJpaRepository;

    @Test
    void shouldSearchContatoByQueryAndProfissional() {
        Profissional profissional = new Profissional();
        profissional.setNome("Joana");
        profissional.setCargo("Dev");
        profissional.setNascimento(new Date());
        profissional.setAtivo(true);
        profissionalJpaRepository.save(profissional);

        Contato contato = new Contato();
        contato.setNome("Whatsapp");
        contato.setContato("9999-9999");
        contato.setCreatedDate(new Date());
        contato.setProfissional(profissional);
        contatoJpaRepository.save(contato);

        List<Contato> byQuery = contatoJpaRepository.findByQuery("What");
        assertThat(byQuery).hasSize(1);

        List<Contato> byNomeContatoProfissional = contatoJpaRepository
                .findContatoByNomeAndContatoAndProfissional("Whatsapp", "9999-9999", profissional);
        assertThat(byNomeContatoProfissional).hasSize(1);
        assertThat(byNomeContatoProfissional.get(0).getProfissional().getNome()).isEqualTo("Joana");
    }
}
