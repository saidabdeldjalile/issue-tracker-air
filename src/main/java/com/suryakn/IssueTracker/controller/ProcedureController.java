package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.ProcedureDTO;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.Procedure;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.service.ProcedureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/procedures")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProcedureController {

    private final ProcedureService procedureService;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<ProcedureDTO>> getAllProcedures() {
        List<ProcedureDTO> procedures = procedureService.getAllActiveProcedures().stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProcedureDTO>> getProceduresPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Procedure> procedurePage = procedureService.getAllActiveProceduresPaginated(pageable);
        Page<ProcedureDTO> dtoPage = procedurePage.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedureDTO> getProcedureById(@PathVariable Long id) {
        return procedureService.getProcedureById(id)
                .map(procedure -> ResponseEntity.ok(toDTO(procedure)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProcedureDTO>> searchProcedures(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Procedure> procedurePage = procedureService.searchProcedures(query, pageable);
        Page<ProcedureDTO> dtoPage = procedurePage.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProcedureDTO>> getProceduresByCategory(@PathVariable String category) {
        List<ProcedureDTO> procedures = procedureService.getProceduresByCategory(category).stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(procedureService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<ProcedureDTO> createProcedure(@Valid @RequestBody ProcedureDTO procedureDTO) {
        Procedure procedure = toEntity(procedureDTO);
        Procedure created = procedureService.createProcedure(procedure);
        return ResponseEntity.ok(toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcedureDTO> updateProcedure(@PathVariable Long id, @Valid @RequestBody ProcedureDTO procedureDTO) {
        Procedure procedure = toEntity(procedureDTO);
        Procedure updated = procedureService.updateProcedure(id, procedure);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcedure(@PathVariable Long id) {
        procedureService.deleteProcedure(id);
        return ResponseEntity.noContent().build();
    }

    private ProcedureDTO toDTO(Procedure procedure) {
        return ProcedureDTO.builder()
                .id(procedure.getId())
                .title(procedure.getTitle())
                .content(procedure.getContent())
                .category(procedure.getCategory())
                .description(procedure.getDescription())
                .departmentId(procedure.getDepartment() != null ? procedure.getDepartment().getId() : null)
                .departmentName(procedure.getDepartment() != null ? procedure.getDepartment().getName() : null)
                .active(procedure.isActive())
                .documentPath(procedure.getDocumentPath())
                .createdAt(procedure.getCreatedAt() != null ? procedure.getCreatedAt().toString() : null)
                .modifiedAt(procedure.getModifiedAt() != null ? procedure.getModifiedAt().toString() : null)
                .build();
    }

    private Procedure toEntity(ProcedureDTO dto) {
        Department department = dto.getDepartmentId() != null
                ? departmentRepository.findById(dto.getDepartmentId()).orElse(null)
                : null;

        return Procedure.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .department(department)
                .active(dto.isActive())
                .documentPath(dto.getDocumentPath())
                .build();
    }
}
