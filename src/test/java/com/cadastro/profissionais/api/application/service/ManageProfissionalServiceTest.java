package com.cadastro.profissionais.api.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.converter.ProfissionalConverter;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManageProfissionalServiceTest {

    private ProfissionalRepositoryPort profissionalRepository;
    private ProfissionalConverter profissionalConverter;
    private ContatoConverter contatoConverter;
    private ManageProfissionalService service;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setup() {
        profissionalRepository = mock(ProfissionalRepositoryPort.class);
        profissionalConverter = mock(ProfissionalConverter.class);
        contatoConverter = mock(ContatoConverter.class);
        service = new ManageProfissionalService(profissionalRepository, profissionalConverter, contatoConverter);

        logger = (Logger) LoggerFactory.getLogger(ManageProfissionalService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void shouldLogSuccessWhenCreatingProfessional() {
        Profissional profissional = new Profissional();
        profissional.setId(5L);
        ProfissionalRequestDTO request = new ProfissionalRequestDTO();
        request.setNome("Ana");
        request.setCargo("Dev");
        request.setContatos(Collections.singletonList(new Contato()));

        when(profissionalRepository.findByNomeCargoNascimento(request.getNome(), request.getCargo(), request.getNascimento()))
                .thenReturn(Collections.emptyList());
        when(profissionalConverter.toEntity(request)).thenReturn(profissional);
        when(contatoConverter.toEntities(request.getContatos(), profissional)).thenReturn(Collections.emptyList());
        when(profissionalRepository.save(profissional)).thenReturn(profissional);

        ResponseEntity<String> response = service.createProfissional(request);

        assertThat(response.getBody()).contains("Sucesso");
        assertThat(listAppender.list.stream()
                .filter(event -> event.getLevel().equals(Level.INFO))
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains("Professional created with id=5"))).isTrue();
    }

    @Test
    void shouldLogErrorWhenGettingProfessionalsFails() {
        when(profissionalRepository.findAll()).thenThrow(new RuntimeException("db down"));

        assertThrows(RuntimeException.class, () -> service.getProfissionais(null, null));

        assertThat(listAppender.list.stream()
                .filter(event -> event.getLevel().equals(Level.ERROR))
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains("Error retrieving professionals"))).isTrue();
    }

    @Test
    void shouldLogOutcomeWhenUpdatingProfessional() {
        Profissional profissional = new Profissional();
        profissional.setId(10L);
        ProfissionalRequestDTO request = new ProfissionalRequestDTO();
        request.setNome("Rita");
        request.setCargo("Analyst");

        when(profissionalRepository.findById(10L)).thenReturn(Optional.of(profissional));

        String message = service.updateProfissional(10L, request);

        assertThat(message).contains("Sucesso");
        assertThat(listAppender.list.stream()
                .filter(event -> event.getLevel().equals(Level.INFO))
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Finished updating professional id=10"))).isTrue();
    }
}
