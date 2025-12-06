package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.application.port.in.ManageProfissionalUseCase;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfissionalController.class)
class ProfissionalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManageProfissionalUseCase manageProfissionalUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListProfissionaisWithQueryParams() throws Exception {
        ProfissionalResponseDTO responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("John Doe");

        Mockito.when(manageProfissionalUseCase.getProfissionais("doe", List.of("id")))
                .thenReturn(List.of(responseDTO));

        MvcResult result = mockMvc.perform(get("/profissionais?q=doe&fields=id"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("John Doe");
    }

    @Test
    void shouldReturnProfissionalById() throws Exception {
        ProfissionalResponseDTO responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(1L);
        Mockito.when(manageProfissionalUseCase.getProfissionalById(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturnNotFoundWhenProfissionalMissing() throws Exception {
        Mockito.when(manageProfissionalUseCase.getProfissionalById(9L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profissionais/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProfissional() throws Exception {
        ProfissionalRequestDTO request = new ProfissionalRequestDTO();
        request.setNome("Jane");
        Mockito.when(manageProfissionalUseCase.createProfissional(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok("created"));

        mockMvc.perform(post("/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("created"));
    }

    @Test
    void shouldUpdateProfissional() throws Exception {
        Mockito.when(manageProfissionalUseCase.updateProfissional(any(), any())).thenReturn("updated");

        mockMvc.perform(put("/profissionais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProfissionalRequestDTO())))
                .andExpect(status().isOk())
                .andExpect(content().string("updated"));
    }

    @Test
    void shouldDeleteProfissional() throws Exception {
        Mockito.when(manageProfissionalUseCase.deleteProfissional(1L)).thenReturn("deleted");

        mockMvc.perform(delete("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }
}
