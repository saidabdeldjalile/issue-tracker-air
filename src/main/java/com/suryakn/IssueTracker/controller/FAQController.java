package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.FAQDTO;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.FAQ;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.service.FAQService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faqs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FAQController {

    private final FAQService faqService;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<FAQDTO>> getAllFAQs() {
        try {
            List<FAQDTO> faqs = faqService.getAllActiveFAQs().stream()
                    .map(this::toDTO)
                    .toList();
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            // Temporary fix: return empty list if database error
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<FAQDTO>> getFAQsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FAQ> faqPage = faqService.getAllActiveFAQsPaginated(pageable);
        Page<FAQDTO> dtoPage = faqPage.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FAQDTO> getFAQById(@PathVariable Long id) {
        return faqService.getFAQById(id)
                .map(faq -> ResponseEntity.ok(toDTO(faq)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FAQDTO>> searchFAQs(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FAQ> faqPage = faqService.searchFAQs(query, pageable);
        Page<FAQDTO> dtoPage = faqPage.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FAQDTO>> getFAQsByCategory(@PathVariable String category) {
        List<FAQDTO> faqs = faqService.getFAQsByCategory(category).stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(faqService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<FAQDTO> createFAQ(@Valid @RequestBody FAQDTO faqDTO) {
        FAQ faq = toEntity(faqDTO);
        FAQ created = faqService.createFAQ(faq);
        return ResponseEntity.ok(toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FAQDTO> updateFAQ(@PathVariable Long id, @Valid @RequestBody FAQDTO faqDTO) {
        FAQ faq = toEntity(faqDTO);
        FAQ updated = faqService.updateFAQ(id, faq);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        faqService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    private FAQDTO toDTO(FAQ faq) {
        return FAQDTO.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .keywords(faq.getKeywords())
                .departmentId(faq.getDepartment() != null ? faq.getDepartment().getId() : null)
                .departmentName(faq.getDepartment() != null ? faq.getDepartment().getName() : null)
                .active(faq.isActive())
                .viewCount(faq.getViewCount())
                .createdAt(faq.getCreatedAt() != null ? faq.getCreatedAt().toString() : null)
                .modifiedAt(faq.getModifiedAt() != null ? faq.getModifiedAt().toString() : null)
                .build();
    }

    private FAQ toEntity(FAQDTO dto) {
        Department department = dto.getDepartmentId() != null
                ? departmentRepository.findById(dto.getDepartmentId()).orElse(null)
                : null;

        return FAQ.builder()
                .id(dto.getId())
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .category(dto.getCategory())
                .keywords(dto.getKeywords())
                .department(department)
                .active(dto.isActive())
                .viewCount(dto.getViewCount() != null ? dto.getViewCount() : 0)
                .build();
    }
}
