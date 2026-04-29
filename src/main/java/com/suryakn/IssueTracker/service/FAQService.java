package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.FAQ;
import com.suryakn.IssueTracker.repository.FAQRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;

    // @Cacheable(value = "faqs", key = "'all-active'") // Temporarily disabled
    public List<FAQ> getAllActiveFAQs() {
        try {
            log.debug("Fetching all active FAQs from database");
            return faqRepository.findByActiveTrue();
        } catch (Exception e) {
            log.error("Error fetching active FAQs: ", e);
            return List.of(); // Return empty list if table doesn't exist
        }
    }

    // @Cacheable(value = "faqs", key = "'paged:' + #pageable.pageNumber + ':' + #pageable.pageSize") // Temporarily disabled
    public Page<FAQ> getAllActiveFAQsPaginated(Pageable pageable) {
        try {
            log.debug("Fetching paginated active FAQs from database: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
            return faqRepository.findByActiveTrue(pageable);
        } catch (Exception e) {
            log.error("Error fetching paginated active FAQs: ", e);
            return Page.empty(); // Return empty page if table doesn't exist
        }
    }

    @Cacheable(value = "faqs", key = "'faq:' + #id")
    public Optional<FAQ> getFAQById(Long id) {
        log.debug("Fetching FAQ by ID from database: {}", id);
        return faqRepository.findById(id);
    }

    // @Cacheable(value = "faqs", key = "'category:' + #category") // Temporarily disabled
    public List<FAQ> getFAQsByCategory(String category) {
        try {
            log.debug("Fetching FAQs by category from database: {}", category);
            return faqRepository.findByCategoryAndActive(category);
        } catch (Exception e) {
            log.error("Error fetching FAQs by category: ", e);
            return List.of(); // Return empty list if table doesn't exist
        }
    }

    // @Cacheable(value = "faqs", key = "'search:' + #query + ':' + #pageable.pageNumber + ':' + #pageable.pageSize") // Temporarily disabled
    public Page<FAQ> searchFAQs(String query, Pageable pageable) {
        try {
            log.debug("Searching FAQs from database: {}", query);
            return faqRepository.searchByQuery(query, pageable);
        } catch (Exception e) {
            log.error("Error searching FAQs: ", e);
            return Page.empty(); // Return empty page if table doesn't exist
        }
    }

    @CacheEvict(value = "faqs", allEntries = true)
    @Transactional
    public FAQ createFAQ(FAQ faq) {
        log.info("Creating FAQ: {}", faq.getQuestion());
        faq.setKeywords(normalizeKeywords(faq.getKeywords()));
        return faqRepository.save(faq);
    }

    @CacheEvict(value = "faqs", allEntries = true)
    @Transactional
    public FAQ updateFAQ(Long id, FAQ faqDetails) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found: " + id));
        
        faq.setQuestion(faqDetails.getQuestion());
        faq.setAnswer(faqDetails.getAnswer());
        faq.setCategory(faqDetails.getCategory());
        faq.setKeywords(normalizeKeywords(faqDetails.getKeywords()));
        faq.setDepartment(faqDetails.getDepartment());
        faq.setActive(faqDetails.isActive());
        
        return faqRepository.save(faq);
    }

    @CacheEvict(value = "faqs", allEntries = true)
    @Transactional
    public void deleteFAQ(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found: " + id));
        faq.setActive(false);
        faqRepository.save(faq);
    }

    public List<String> getAllCategories() {
        return List.of("informatique", "materiel", "administratif", "maintenance", "achat", "formation", "autres");
    }

    public void incrementViewCount(Long id) {
        faqRepository.findById(id).ifPresent(faq -> {
            faq.setViewCount(faq.getViewCount() + 1);
            faqRepository.save(faq);
        });
    }

    private List<String> normalizeKeywords(List<String> keywords) {
        if (keywords == null) {
            return List.of();
        }

        return keywords.stream()
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }
}
