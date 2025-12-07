package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ProfissionalConverter;
import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageProfissionalServiceTest {

    @Mock
    private ProfissionalRepositoryPort profissionalRepository;

    @Mock
    private ProfissionalConverter profissionalConverter;

    @Mock
    private ContatoConverter contatoConverter;

    @InjectMocks
    private ManageProfissionalService manageProfissionalService;

    private Profissional ativo;
    private Profissional inativo;

    @BeforeEach
    void setUp() {
        ativo = new Profissional();
        ativo.setId(1L);
        ativo.setNome("John Doe");
        ativo.setCargo("Dev");
        ativo.setAtivo(true);

        inativo = new Profissional();
        inativo.setId(2L);
        inativo.setNome("Jane Doe");
        inativo.setCargo("QA");
        inativo.setAtivo(false);
    }

    @Test
    void shouldReturnFilteredProfissionaisWhenQueryProvided() {
        ProfissionalResponseDTO responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(ativo.getId());
        responseDTO.setNome(ativo.getNome());

        when(profissionalRepository.findByQuery("doe")).thenReturn(Arrays.asList(ativo, inativo));
        when(profissionalConverter.toResponseDto(ativo)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = manageProfissionalService.getProfissionais("doe", List.of("id", "nome"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ativo.getId());
        assertThat(result.get(0).getNome()).isEqualTo(ativo.getNome());
        verify(profissionalRepository).findByQuery("doe");
    }

    @Test
    void shouldReturnProfissionalByIdWhenActive() {
        ProfissionalResponseDTO responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(ativo.getId());

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(ativo));
        when(profissionalConverter.toResponseDto(ativo)).thenReturn(responseDTO);

        Optional<ProfissionalResponseDTO> result = manageProfissionalService.getProfissionalById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyWhenProfissionalInactive() {
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(inativo));

        Optional<ProfissionalResponseDTO> result = manageProfissionalService.getProfissionalById(2L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCreateNewProfissionalWhenNotExists() {
        ProfissionalRequestDTO requestDTO = new ProfissionalRequestDTO();
        requestDTO.setNome("John Doe");
        requestDTO.setCargo("Dev");

        Profissional entity = new Profissional();
        entity.setId(10L);

        when(profissionalRepository.findByNomeCargoNascimento(any(), any(), any())).thenReturn(List.of());
        when(profissionalConverter.toEntity(requestDTO)).thenReturn(entity);
        when(contatoConverter.toEntities(any(), any())).thenReturn(List.of());
        when(profissionalRepository.save(entity)).thenReturn(entity);

        ResponseEntity<String> response = manageProfissionalService.createProfissional(requestDTO);

        assertThat(response.getBody()).contains("Sucesso, profissional com id 10 cadastrado");
        ArgumentCaptor<Profissional> captor = ArgumentCaptor.forClass(Profissional.class);
        verify(profissionalRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedDate()).isNotNull();
        assertThat(captor.getValue().getAtivo()).isTrue();
    }

    @Test
    void shouldNotCreateProfissionalWhenDuplicate() {
        ProfissionalRequestDTO requestDTO = new ProfissionalRequestDTO();
        when(profissionalRepository.findByNomeCargoNascimento(any(), any(), any())).thenReturn(List.of(ativo));

        ResponseEntity<String> response = manageProfissionalService.createProfissional(requestDTO);

        assertThat(response.getBody()).isEqualTo("Contato já está cadastrado.");
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExistingActiveProfissional() {
        ProfissionalRequestDTO requestDTO = new ProfissionalRequestDTO();
        requestDTO.setNome("Updated");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(ativo));
        when(profissionalRepository.save(ativo)).thenReturn(ativo);

        String response = manageProfissionalService.updateProfissional(1L, requestDTO);

        assertThat(response).isEqualTo("Sucesso, cadastro alterado");
        verify(profissionalConverter).updateEntity(ativo, requestDTO);
        verify(profissionalRepository).save(ativo);
        assertThat(ativo.getCreatedDate()).isInstanceOf(Date.class);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingProfissional() {
        when(profissionalRepository.findById(3L)).thenReturn(Optional.empty());

        String response = manageProfissionalService.updateProfissional(3L, new ProfissionalRequestDTO());

        assertThat(response).isEqualTo("Profissional não encontrado");
    }

    @Test
    void shouldDeleteProfissionalWhenActive() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(ativo));

        String response = manageProfissionalService.deleteProfissional(1L);

        assertThat(response).isEqualTo("Sucesso, profissional excluído");
        verify(profissionalRepository).save(ativo);
        assertThat(ativo.getAtivo()).isFalse();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingProfissional() {
        when(profissionalRepository.findById(4L)).thenReturn(Optional.empty());

        String response = manageProfissionalService.deleteProfissional(4L);

        assertThat(response).isEqualTo("Profissional não encontrado");
    }
}
