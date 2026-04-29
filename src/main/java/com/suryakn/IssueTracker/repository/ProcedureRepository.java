package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.Procedure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    List<Procedure> findByActiveTrue();

    List<Procedure> findByCategory(String category);

    Page<Procedure> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Procedure p WHERE p.active = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Procedure> searchByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Procedure p WHERE p.active = true AND p.category = :category")
    List<Procedure> findByCategoryAndActive(@Param("category") String category);

    List<Procedure> findByDepartmentIdAndActiveTrue(Long departmentId);
}