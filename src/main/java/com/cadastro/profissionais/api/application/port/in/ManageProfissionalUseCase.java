package com.cadastro.profissionais.api.application.port.in;

import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ManageProfissionalUseCase {
    List<ProfissionalResponseDTO> getProfissionais(String q, List<String> fields);

    Optional<ProfissionalResponseDTO> getProfissionalById(Long id);

    ResponseEntity<String> createProfissional(ProfissionalRequestDTO profissionalRequest);

    String updateProfissional(Long id, ProfissionalRequestDTO profissionalRequest);

    String deleteProfissional(Long id);
}
