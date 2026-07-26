package com.dsar.web;

import com.dsar.domain.AuditLog;
import com.dsar.dto.AuditLogDto;
import com.dsar.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
  @RequestMapping("/api/audit-logs")
  @RequiredArgsConstructor
  @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
  public class AuditLogController {

private final AuditLogRepository auditLogRepository;

@GetMapping("/{requestId}")
    public List<AuditLogDto> forRequest(@PathVariable Long requestId) {
      return auditLogRepository.findByRequest_IdOrderByCreatedAtAsc(requestId)
        .stream()
        .map(this::toDto)
        .toList();
    }

private AuditLogDto toDto(AuditLog log) {
  return new AuditLogDto(
    log.getId(),
    log.getRequest().getId(),
    log.getPerformedBy() != null ? log.getPerformedBy().getEmail() : null,
    log.getAction(),
    log.getDetails(),
    log.getCreatedAt()
    );
}
  }
