package com.dsar.dto;

import com.dsar.domain.RequestStatus;
import com.dsar.domain.RequestType;

import java.time.Instant;

public class DsarRequestDtos {

public record CreateRequestDto(
  String subjectName,
  String subjectEmail,
  RequestType type,
  String description
  ) {}

public record StatusChangeDto(RequestStatus newStatus, String note) {}

public record AssignDto(Long userId) {}

public record RequestResponseDto(
  Long id,
  String referenceCode,
  String subjectName,
  String subjectEmail,
  RequestType type,
  RequestStatus status,
  String description,
  Long assignedToId,
  String assignedToName,
  Instant receivedAt,
  Instant legalDeadline,
  Instant completedAt,
  boolean overdue
  ) {}
}
