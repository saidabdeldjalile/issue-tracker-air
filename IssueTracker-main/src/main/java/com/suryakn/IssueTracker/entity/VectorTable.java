package com.suryakn.IssueTracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VectorTable {

    @Id
    @GeneratedValue
    private Long id;

    private Long ticketId;
    private Long projectId;
    @Column(columnDefinition = "TEXT")
    private String vector;
}
