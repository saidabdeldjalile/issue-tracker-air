package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.entity.Priority;
import com.suryakn.IssueTracker.service.SlaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sla")
@RequiredArgsConstructor
@Tag(name = "SLA", description = "API de suivi des accords de niveau de service")
public class SlaController {

    private final SlaService slaService;

    @GetMapping("/metrics")
    @Operation(summary = "Obtenir les métriques SLA globales")
    public ResponseEntity<SlaService.SlaMetrics> getMetrics() {
        return ResponseEntity.ok(slaService.getSlaMetrics());
    }

    @GetMapping("/metrics/priority/{priority}")
    @Operation(summary = "Obtenir les métriques SLA par priorité")
    public ResponseEntity<SlaService.SlaMetrics> getMetricsByPriority(@PathVariable Priority priority) {
        return ResponseEntity.ok(slaService.getSlaMetricsByPriority(priority));
    }

    @GetMapping("/metrics/department/{departmentId}")
    @Operation(summary = "Obtenir les métriques SLA par département")
    public ResponseEntity<SlaService.SlaMetrics> getMetricsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(slaService.getSlaMetricsByDepartment(departmentId));
    }

    @GetMapping("/breached")
    @Operation(summary = "Liste des tickets avec SLA dépassé")
    public ResponseEntity<List<SlaService.TicketSlaStatus>> getBreachedTickets() {
        return ResponseEntity.ok(slaService.getBreachedTickets());
    }

    @GetMapping("/at-risk")
    @Operation(summary = "Liste des tickets à risque de dépassement SLA")
    public ResponseEntity<List<SlaService.TicketSlaStatus>> getAtRiskTickets() {
        return ResponseEntity.ok(slaService.getAtRiskTickets());
    }
}