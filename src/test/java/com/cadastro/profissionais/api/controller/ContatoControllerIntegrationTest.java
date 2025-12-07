package com.cadastro.profissionais.api.controller;

import com.cadastro.profissionais.api.application.port.in.ManageContatoUseCase;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ContatoController.class)
class ContatoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManageContatoUseCase manageContatoUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListContatos() throws Exception {
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(1L);
        dto.setNome("Telefone");

        Mockito.when(manageContatoUseCase.getContatos("tel", List.of("id"))).thenReturn(List.of(dto));

        mockMvc.perform(get("/contatos?q=tel&fields=id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnContatoById() throws Exception {
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(2L);
        Mockito.when(manageContatoUseCase.getContatoById(2L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/contatos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void shouldReturnNotFoundForContato() throws Exception {
        Mockito.when(manageContatoUseCase.getContatoById(7L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/contatos/7"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateContato() throws Exception {
        ContatoRequestDTO request = new ContatoRequestDTO();
        request.setNome("Telefone");
        request.setContato("123456789");
        Mockito.when(manageContatoUseCase.createContato(Mockito.any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok("created"));

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("created"));
    }

    @Test
    void shouldUpdateContato() throws Exception {
        Mockito.when(manageContatoUseCase.updateContato(Mockito.eq(1L), Mockito.any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok("updated"));

        ContatoRequestDTO request = new ContatoRequestDTO();
        request.setNome("Telefone");
        request.setContato("123456789");

        mockMvc.perform(put("/contatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("updated"));
    }

    @Test
    void shouldReturnBadRequestForInvalidContatoPayload() throws Exception {
        ContatoRequestDTO invalidRequest = new ContatoRequestDTO();
        invalidRequest.setContato("123456789");

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("nome"));
    }

    @Test
    void shouldDeleteContato() throws Exception {
        Mockito.when(manageContatoUseCase.deleteContato(3L))
                .thenReturn(org.springframework.http.ResponseEntity.ok("deleted"));

        mockMvc.perform(delete("/contatos/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }
}
