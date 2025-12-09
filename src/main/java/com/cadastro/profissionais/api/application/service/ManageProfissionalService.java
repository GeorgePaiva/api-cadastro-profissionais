package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ProfissionalConverter;
import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.in.ManageProfissionalUseCase;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.dto.ProfissionalRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ProfissionalResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManageProfissionalService implements ManageProfissionalUseCase {

    private final ProfissionalRepositoryPort profissionalRepository;
    private final ProfissionalConverter profissionalConverter;
    private final ContatoConverter contatoConverter;
    private static final Logger logger = LoggerFactory.getLogger(ManageProfissionalService.class);

    public ManageProfissionalService(ProfissionalRepositoryPort profissionalRepository,
                                     ProfissionalConverter profissionalConverter,
                                     ContatoConverter contatoConverter) {
        this.profissionalRepository = profissionalRepository;
        this.profissionalConverter = profissionalConverter;
        this.contatoConverter = contatoConverter;
    }

    @Override
    public List<ProfissionalResponseDTO> getProfissionais(String q, List<String> fields) {
        Instant start = Instant.now();
        logger.info("Retrieving professionals with query={} and fields={}", q, fields);
        try {
            List<Profissional> profissionais = (q != null && !q.isEmpty())
                    ? profissionalRepository.findByQuery(q)
                    : profissionalRepository.findAll();

            List<ProfissionalResponseDTO> responses = profissionais.stream()
                    .filter(Profissional::getAtivo)
                    .map(profissional -> filterFields(profissionalConverter.toResponseDto(profissional), fields))
                    .collect(Collectors.toList());

            logger.info("Retrieved {} professionals in {} ms", responses.size(), elapsedMillis(start));
            return responses;
        } catch (Exception ex) {
            logger.error("Error retrieving professionals in {} ms", elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public Optional<ProfissionalResponseDTO> getProfissionalById(Long id) {
        Instant start = Instant.now();
        logger.info("Fetching professional by id={}", id);
        try {
            Optional<ProfissionalResponseDTO> profissional = profissionalRepository.findById(id)
                    .filter(Profissional::getAtivo)
                    .map(profissionalConverter::toResponseDto);
            logger.info("Finished fetching professional id={} found={} in {} ms", id, profissional.isPresent(), elapsedMillis(start));
            return profissional;
        } catch (Exception ex) {
            logger.error("Error fetching professional id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public ResponseEntity<String> createProfissional(ProfissionalRequestDTO profissionalRequest) {
        Instant start = Instant.now();
        logger.info("Creating professional with nome={} cargo={} nascimento={} and {} contatos",
                profissionalRequest.getNome(), profissionalRequest.getCargo(), profissionalRequest.getNascimento(),
                profissionalRequest.getContatos() != null ? profissionalRequest.getContatos().size() : 0);
        try {
            List<Profissional> profissionalDB = profissionalRepository.findByNomeCargoNascimento(
                    profissionalRequest.getNome(), profissionalRequest.getCargo(), profissionalRequest.getNascimento());

            if (profissionalDB.isEmpty()) {
                Profissional profissional = profissionalConverter.toEntity(profissionalRequest);
                profissional.setCreatedDate(new Date());
                List<Contato> contatos = contatoConverter.toEntities(profissionalRequest.getContatos(), profissional);
                profissional.setContatos(contatos);
                profissionalRepository.save(profissional);
                logger.info("Professional created with id={} in {} ms", profissional.getId(), elapsedMillis(start));
                return ResponseEntity.ok("Sucesso, profissional com id " + profissional.getId() + " cadastrado");
            }

            logger.info("Professional already registered, skipping creation in {} ms", elapsedMillis(start));
            return ResponseEntity.ok("Contato já está cadastrado.");
        } catch (Exception ex) {
            logger.error("Error creating professional in {} ms", elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public String updateProfissional(Long id, ProfissionalRequestDTO profissionalRequest) {
        Instant start = Instant.now();
        logger.info("Updating professional id={} with nome={} cargo={}", id, profissionalRequest.getNome(), profissionalRequest.getCargo());
        try {
            String response = profissionalRepository.findById(id)
                    .filter(Profissional::getAtivo)
                    .map(profissional -> {
                        profissionalConverter.updateEntity(profissional, profissionalRequest);
                        profissional.setCreatedDate(new Date());
                        profissionalRepository.save(profissional);
                        return "Sucesso, cadastro alterado";
                    })
                    .orElse("Profissional não encontrado");

            logger.info("Finished updating professional id={} with result='{}' in {} ms", id, response, elapsedMillis(start));
            return response;
        } catch (Exception ex) {
            logger.error("Error updating professional id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public String deleteProfissional(Long id) {
        Instant start = Instant.now();
        logger.info("Deleting professional id={}", id);
        try {
            String response = profissionalRepository.findById(id)
                    .filter(Profissional::getAtivo)
                    .map(profissional -> {
                        profissional.setAtivo(false);
                        profissionalRepository.save(profissional);
                        return "Sucesso, profissional excluído";
                    })
                    .orElse("Profissional não encontrado");

            logger.info("Finished deleting professional id={} with result='{}' in {} ms", id, response, elapsedMillis(start));
            return response;
        } catch (Exception ex) {
            logger.error("Error deleting professional id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    private ProfissionalResponseDTO filterFields(ProfissionalResponseDTO dto, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return dto;
        }

        ProfissionalResponseDTO filtered = new ProfissionalResponseDTO();
        if (fields.contains("id")) {
            filtered.setId(dto.getId());
        }
        if (fields.contains("nome")) {
            filtered.setNome(dto.getNome());
        }
        if (fields.contains("cargo")) {
            filtered.setCargo(dto.getCargo());
        }
        if (fields.contains("nascimento")) {
            filtered.setNascimento(dto.getNascimento());
        }
        if (fields.contains("createdDate")) {
            filtered.setCreatedDate(dto.getCreatedDate());
        }
        if (fields.contains("ativo")) {
            filtered.setAtivo(dto.getAtivo());
        }
        if (fields.contains("contatos")) {
            filtered.setContatos(dto.getContatos());
        }
        return filtered;
    }

    private long elapsedMillis(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }
}
