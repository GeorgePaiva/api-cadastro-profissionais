package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.out.ContatoRepositoryPort;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageContatoServiceTest {

    @Mock
    private ContatoRepositoryPort contatoRepository;

    @Mock
    private ProfissionalRepositoryPort profissionalRepository;

    @Mock
    private ContatoConverter contatoConverter;

    @InjectMocks
    private ManageContatoService manageContatoService;

    private Contato contato;
    private Profissional profissional;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
        profissional.setId(5L);
        profissional.setNome("John");

        contato = new Contato();
        contato.setId(1L);
        contato.setNome("Telefone");
        contato.setContato("123456");
        contato.setProfissional(profissional);
    }

    @Test
    void shouldReturnContatosWithFilterFields() {
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(1L);
        dto.setNome("Telefone");

        when(contatoRepository.findByQuery("tel")).thenReturn(List.of(contato));
        when(contatoConverter.toDto(contato)).thenReturn(dto);

        List<ContatoResponseDTO> responses = manageContatoService.getContatos("tel", List.of("id", "nome"));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getNome()).isEqualTo("Telefone");
        verify(contatoRepository).findByQuery("tel");
    }

    @Test
    void shouldReturnContatoById() {
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(1L);
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(contatoConverter.toDto(contato)).thenReturn(dto);

        Optional<ContatoResponseDTO> result = manageContatoService.getContatoById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldCreateContatoWhenNotDuplicate() {
        ContatoRequestDTO requestDTO = new ContatoRequestDTO();
        requestDTO.setNome("Telefone");
        requestDTO.setContato("123456");
        ProfissionalDTO profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setNome("John");
        requestDTO.setProfissional(profissionalDTO);

        when(profissionalRepository.findByNome("John")).thenReturn(profissional);
        when(contatoRepository.findByNomeContatoProfissional(any(), any(), any())).thenReturn(List.of());
        when(contatoConverter.toEntity(requestDTO, profissional)).thenReturn(contato);
        when(contatoRepository.save(contato)).thenReturn(contato);

        ResponseEntity<String> response = manageContatoService.createContato(requestDTO);

        assertThat(response.getBody()).contains("Sucesso, contato com id 1 cadastrado");
        verify(contatoRepository).save(contato);
        assertThat(contato.getCreatedDate()).isInstanceOf(Date.class);
    }

    @Test
    void shouldReturnDuplicateMessageWhenContatoExists() {
        ContatoRequestDTO requestDTO = new ContatoRequestDTO();
        when(contatoRepository.findByNomeContatoProfissional(any(), any(), any())).thenReturn(List.of(contato));

        ResponseEntity<String> response = manageContatoService.createContato(requestDTO);

        assertThat(response.getBody()).isEqualTo("Contato já está cadastrado.");
        verify(contatoRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExistingContato() {
        ContatoRequestDTO requestDTO = new ContatoRequestDTO();
        requestDTO.setNome("Email");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(contatoRepository.save(contato)).thenReturn(contato);

        ResponseEntity<String> response = manageContatoService.updateContato(1L, requestDTO);

        assertThat(response.getBody()).isEqualTo("Sucesso, cadastro alterado");
        verify(contatoConverter).updateEntity(contato, requestDTO);
        verify(contatoRepository).save(contato);
        assertThat(contato.getCreatedDate()).isInstanceOf(Date.class);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingContato() {
        when(contatoRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = manageContatoService.updateContato(2L, new ContatoRequestDTO());

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldDeleteContatoWhenExists() {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));

        ResponseEntity<String> response = manageContatoService.deleteContato(1L);

        assertThat(response.getBody()).isEqualTo("Sucesso, contato excluído");
        verify(contatoRepository).delete(contato);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingContato() {
        when(contatoRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = manageContatoService.deleteContato(3L);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
