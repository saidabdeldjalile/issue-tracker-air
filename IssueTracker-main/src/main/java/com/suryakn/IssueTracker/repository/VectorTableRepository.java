package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.VectorTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VectorTableRepository extends JpaRepository<VectorTable, Long> {

    List<VectorTable> findAllByProjectId(Long projectId);

    void deleteByTicketId(Long id);
}
