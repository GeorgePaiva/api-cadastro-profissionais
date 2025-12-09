package com.cadastro.profissionais.api.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.out.ContatoRepositoryPort;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManageContatoServiceTest {

    private ContatoRepositoryPort contatoRepository;
    private ProfissionalRepositoryPort profissionalRepository;
    private ContatoConverter contatoConverter;
    private ManageContatoService service;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setup() {
        contatoRepository = mock(ContatoRepositoryPort.class);
        profissionalRepository = mock(ProfissionalRepositoryPort.class);
        contatoConverter = mock(ContatoConverter.class);
        service = new ManageContatoService(contatoRepository, profissionalRepository, contatoConverter);

        logger = (Logger) LoggerFactory.getLogger(ManageContatoService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void shouldLogDeletionOutcome() {
        Contato contato = new Contato();
        contato.setId(2L);
        when(contatoRepository.findById(2L)).thenReturn(Optional.of(contato));

        ResponseEntity<String> response = service.deleteContato(2L);

        assertThat(response.getBody()).contains("Sucesso");
        assertThat(listAppender.list.stream()
                .filter(event -> event.getLevel().equals(Level.INFO))
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Finished deleting contato id=2"))).isTrue();
    }

    @Test
    void shouldLogErrorOnUpdateFailure() {
        when(contatoRepository.findById(3L)).thenThrow(new IllegalStateException("db"));
        ContatoRequestDTO request = new ContatoRequestDTO();
        request.setNome("Contato");
        request.setProfissional(new ProfissionalDTO());

        assertThrows(IllegalStateException.class, () -> service.updateContato(3L, request));

        assertThat(listAppender.list.stream()
                .filter(event -> event.getLevel().equals(Level.ERROR))
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Error updating contato id=3"))).isTrue();
    }
}
