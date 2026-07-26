package com.dsar.repository;

import com.dsar.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
* Audit log entries are append-only. The delete methods inherited from
  * JpaRepository are intentionally overridden below to throw, guaranteeing
  * the audit trail can never be mutated or removed through this repository.
  */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

List<AuditLog> findByRequest_IdOrderByCreatedAtAsc(Long requestId);

@Override
  default void deleteById(Long id) {
    throw new UnsupportedOperationException("Audit log entries are immutable and cannot be deleted");
  }

@Override
  default void delete(AuditLog entity) {
    throw new UnsupportedOperationException("Audit log entries are immutable and cannot be deleted");
  }
}
