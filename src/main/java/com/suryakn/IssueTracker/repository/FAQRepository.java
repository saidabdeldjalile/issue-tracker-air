package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {

    List<FAQ> findByActiveTrue();

    List<FAQ> findByCategory(String category);

    Page<FAQ> findByActiveTrue(Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE f.active = true AND " +
           "(LOWER(f.question) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<FAQ> searchByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE f.active = true AND f.category = :category")
    List<FAQ> findByCategoryAndActive(@Param("category") String category);

    List<FAQ> findByDepartmentIdAndActiveTrue(Long departmentId);
}