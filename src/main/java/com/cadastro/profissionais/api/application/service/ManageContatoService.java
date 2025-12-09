package com.cadastro.profissionais.api.application.service;

import com.cadastro.profissionais.api.application.converter.ContatoConverter;
import com.cadastro.profissionais.api.application.port.in.ManageContatoUseCase;
import com.cadastro.profissionais.api.application.port.out.ContatoRepositoryPort;
import com.cadastro.profissionais.api.application.port.out.ProfissionalRepositoryPort;
import com.cadastro.profissionais.api.domain.Contato;
import com.cadastro.profissionais.api.domain.Profissional;
import com.cadastro.profissionais.api.domain.dto.ContatoRequestDTO;
import com.cadastro.profissionais.api.domain.dto.ContatoResponseDTO;
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
public class ManageContatoService implements ManageContatoUseCase {

    private final ContatoRepositoryPort contatoRepository;
    private final ProfissionalRepositoryPort profissionalRepository;
    private final ContatoConverter contatoConverter;
    private static final Logger logger = LoggerFactory.getLogger(ManageContatoService.class);

    public ManageContatoService(ContatoRepositoryPort contatoRepository,
                                ProfissionalRepositoryPort profissionalRepository,
                                ContatoConverter contatoConverter) {
        this.contatoRepository = contatoRepository;
        this.profissionalRepository = profissionalRepository;
        this.contatoConverter = contatoConverter;
    }

    @Override
    public List<ContatoResponseDTO> getContatos(String q, List<String> fields) {
        Instant start = Instant.now();
        logger.info("Retrieving contatos with query={} and fields={}", q, fields);
        try {
            List<Contato> contatos = (q != null && !q.isEmpty())
                    ? contatoRepository.findByQuery(q)
                    : contatoRepository.findAll();

            List<ContatoResponseDTO> responses = contatos.stream()
                    .map(contato -> filterFields(contatoConverter.toDto(contato), fields))
                    .collect(Collectors.toList());

            logger.info("Retrieved {} contatos in {} ms", responses.size(), elapsedMillis(start));
            return responses;
        } catch (Exception ex) {
            logger.error("Error retrieving contatos in {} ms", elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public Optional<ContatoResponseDTO> getContatoById(Long id) {
        Instant start = Instant.now();
        logger.info("Fetching contato by id={}", id);
        try {
            Optional<ContatoResponseDTO> contato = contatoRepository.findById(id)
                    .map(contatoConverter::toDto);
            logger.info("Finished fetching contato id={} found={} in {} ms", id, contato.isPresent(), elapsedMillis(start));
            return contato;
        } catch (Exception ex) {
            logger.error("Error fetching contato id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public ResponseEntity<String> createContato(ContatoRequestDTO contatoRequest) {
        Instant start = Instant.now();
        logger.info("Creating contato with nome={} for profissional={} and {} caracteres in contato field", contatoRequest.getNome(),
                contatoRequest.getProfissional() != null ? contatoRequest.getProfissional().getNome() : "desconhecido",
                contatoRequest.getContato() != null ? contatoRequest.getContato().length() : 0);
        try {
            Profissional profissional = findProfissional(contatoRequest);

            List<Contato> contatoDB = contatoRepository.findByNomeContatoProfissional(
                    contatoRequest.getNome(), contatoRequest.getContato(), profissional);

            if (contatoDB.isEmpty()) {
                Contato contato = contatoConverter.toEntity(contatoRequest, profissional);
                contato.setCreatedDate(new Date());
                contatoRepository.save(contato);
                logger.info("Contato created with id={} in {} ms", contato.getId(), elapsedMillis(start));
                return ResponseEntity.ok("Sucesso, contato com id " + contato.getId() + " cadastrado");
            }

            logger.info("Contato already registered, skipping creation in {} ms", elapsedMillis(start));
            return ResponseEntity.ok("Contato já está cadastrado.");
        } catch (Exception ex) {
            logger.error("Error creating contato in {} ms", elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public ResponseEntity<String> updateContato(Long id, ContatoRequestDTO contatoRequest) {
        Instant start = Instant.now();
        logger.info("Updating contato id={} with nome={} for profissional={}", id, contatoRequest.getNome(),
                contatoRequest.getProfissional() != null ? contatoRequest.getProfissional().getNome() : "desconhecido");
        try {
            ResponseEntity<String> response = contatoRepository.findById(id)
                    .map(contato -> {
                        contatoConverter.updateEntity(contato, contatoRequest);
                        contato.setCreatedDate(new Date());
                        contatoRepository.save(contato);
                        return ResponseEntity.ok("Sucesso, cadastro alterado");
                    })
                    .orElse(ResponseEntity.notFound().build());

            logger.info("Finished updating contato id={} with status={} in {} ms", id, response.getStatusCode(), elapsedMillis(start));
            return response;
        } catch (Exception ex) {
            logger.error("Error updating contato id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    @Override
    public ResponseEntity<String> deleteContato(Long id) {
        Instant start = Instant.now();
        logger.info("Deleting contato id={}", id);
        try {
            ResponseEntity<String> response = contatoRepository.findById(id)
                    .map(contato -> {
                        contatoRepository.delete(contato);
                        return ResponseEntity.ok("Sucesso, contato excluído");
                    })
                    .orElse(ResponseEntity.notFound().build());

            logger.info("Finished deleting contato id={} with status={} in {} ms", id, response.getStatusCode(), elapsedMillis(start));
            return response;
        } catch (Exception ex) {
            logger.error("Error deleting contato id={} in {} ms", id, elapsedMillis(start), ex);
            throw ex;
        }
    }

    private ContatoResponseDTO filterFields(ContatoResponseDTO dto, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return dto;
        }

        ContatoResponseDTO filtered = new ContatoResponseDTO();
        if (fields.contains("id")) {
            filtered.setId(dto.getId());
        }
        if (fields.contains("nome")) {
            filtered.setNome(dto.getNome());
        }
        if (fields.contains("contato")) {
            filtered.setContato(dto.getContato());
        }
        if (fields.contains("createdDate")) {
            filtered.setCreatedDate(dto.getCreatedDate());
        }
        if (fields.contains("profissional")) {
            filtered.setProfissional(dto.getProfissional());
        }
        return filtered;
    }

    private Profissional findProfissional(ContatoRequestDTO contatoRequest) {
        if (contatoRequest.getProfissional() != null && contatoRequest.getProfissional().getNome() != null) {
            return profissionalRepository.findByNome(contatoRequest.getProfissional().getNome());
        }
        return null;
    }

    private long elapsedMillis(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }
}
