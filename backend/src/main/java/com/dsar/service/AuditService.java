package com.dsar.service;

import com.dsar.domain.AuditAction;
import com.dsar.domain.AuditLog;
import com.dsar.domain.DsarRequest;
import com.dsar.domain.User;
import com.dsar.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
  @RequiredArgsConstructor
  public class AuditService {

private final AuditLogRepository auditLogRepository;

public void log(DsarRequest request, User performedBy, AuditAction action, String details) {
  AuditLog entry = AuditLog.builder()
    .request(request)
    .performedBy(performedBy)
    .action(action)
    .details(details)
    .build();
  auditLogRepository.save(entry);
}
  }
