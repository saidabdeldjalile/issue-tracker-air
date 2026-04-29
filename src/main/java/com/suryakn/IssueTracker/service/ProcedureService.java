package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.Procedure;
import com.suryakn.IssueTracker.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    @Cacheable(value = "procedures", key = "'all-active'")
    public List<Procedure> getAllActiveProcedures() {
        log.debug("Fetching all active procedures from database");
        return procedureRepository.findByActiveTrue();
    }

    @Cacheable(value = "procedures", key = "'paged:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<Procedure> getAllActiveProceduresPaginated(Pageable pageable) {
        log.debug("Fetching paginated active procedures from database: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return procedureRepository.findByActiveTrue(pageable);
    }

    @Cacheable(value = "procedures", key = "'proc:' + #id")
    public Optional<Procedure> getProcedureById(Long id) {
        log.debug("Fetching procedure by ID from database: {}", id);
        return procedureRepository.findById(id);
    }

    @Cacheable(value = "procedures", key = "'category:' + #category")
    public List<Procedure> getProceduresByCategory(String category) {
        log.debug("Fetching procedures by category from database: {}", category);
        return procedureRepository.findByCategoryAndActive(category);
    }

    @Cacheable(value = "procedures", key = "'search:' + #query + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<Procedure> searchProcedures(String query, Pageable pageable) {
        log.debug("Searching procedures from database: {}", query);
        return procedureRepository.searchByQuery(query, pageable);
    }

    @CacheEvict(value = "procedures", allEntries = true)
    @Transactional
    public Procedure createProcedure(Procedure procedure) {
        log.info("Creating Procedure: {}", procedure.getTitle());
        return procedureRepository.save(procedure);
    }

    @CacheEvict(value = "procedures", allEntries = true)
    @Transactional
    public Procedure updateProcedure(Long id, Procedure procedureDetails) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedure not found: " + id));
        
        procedure.setTitle(procedureDetails.getTitle());
        procedure.setContent(procedureDetails.getContent());
        procedure.setCategory(procedureDetails.getCategory());
        procedure.setDescription(procedureDetails.getDescription());
        procedure.setDepartment(procedureDetails.getDepartment());
        procedure.setActive(procedureDetails.isActive());
        procedure.setDocumentPath(procedureDetails.getDocumentPath());
        
        return procedureRepository.save(procedure);
    }

    @CacheEvict(value = "procedures", allEntries = true)
    @Transactional
    public void deleteProcedure(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedure not found: " + id));
        procedure.setActive(false);
        procedureRepository.save(procedure);
    }

    public List<String> getAllCategories() {
        return List.of("informatique", "materiel", "administratif", "maintenance", "achat", "formation", "autres");
    }
}
