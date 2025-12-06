package com.cadastro.profissionais.api.application.port.in;

import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ManageContatoUseCase {
    List<ContatoResponseDTO> getContatos(String q, List<String> fields);

    Optional<ContatoResponseDTO> getContatoById(Long id);

    ResponseEntity<String> createContato(ContatoRequestDTO contatoRequest);

    ResponseEntity<String> updateContato(Long id, ContatoRequestDTO contatoRequest);

    ResponseEntity<String> deleteContato(Long id);
}
