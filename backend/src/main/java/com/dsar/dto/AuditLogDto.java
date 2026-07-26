package com.dsar.dto;

import com.dsar.domain.AuditAction;

import java.time.Instant;

public record AuditLogDto(
  Long id,
  Long requestId,
  String performedByEmail,
  AuditAction action,
  String details,
  Instant createdAt
  ) {}
