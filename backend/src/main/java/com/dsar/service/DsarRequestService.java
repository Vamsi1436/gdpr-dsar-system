package com.dsar.service;

import com.dsar.domain.AuditAction;
import com.dsar.domain.DsarRequest;
import com.dsar.domain.RequestStatus;
import com.dsar.domain.User;
import com.dsar.dto.DsarRequestDtos.CreateRequestDto;
import com.dsar.dto.DsarRequestDtos.RequestResponseDto;
import com.dsar.exception.ResourceNotFoundException;
import com.dsar.repository.DsarRequestRepository;
import com.dsar.repository.UserRepository;
import com.dsar.workflow.RequestStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
  @RequiredArgsConstructor
  public class DsarRequestService {

private static final SecureRandom RANDOM = new SecureRandom();

private final DsarRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestStateMachine stateMachine;
    private final AuditService auditService;
    private final EmailService emailService;

@Transactional
    public RequestResponseDto createRequest(CreateRequestDto dto, User createdBy) {
      Instant now = Instant.now();
      DsarRequest request = DsarRequest.builder()
        .referenceCode(generateReferenceCode())
        .subjectName(dto.subjectName())
        .subjectEmail(dto.subjectEmail())
        .type(dto.type())
        .status(RequestStatus.RECEIVED)
        .description(dto.description())
        .receivedAt(now)
        .legalDeadline(now.plus(30, ChronoUnit.DAYS))
        .build();

    request = requestRepository.save(request);
      auditService.log(request, createdBy, AuditAction.REQUEST_CREATED,
                       "Request logged for " + dto.subjectEmail());

    return toDto(request);
    }

@Transactional(readOnly = true)
    public List<RequestResponseDto> listAll() {
      return requestRepository.findAll().stream().map(this::toDto).toList();
    }

@Transactional(readOnly = true)
    public RequestResponseDto getById(Long id) {
      return toDto(findEntity(id));
    }

@Transactional(readOnly = true)
    public DsarRequest getEntityById(Long id) {
      return findEntity(id);
    }

@Transactional
    public RequestResponseDto assign(Long requestId, Long userId, User actor) {
      DsarRequest request = findEntity(requestId);
      User assignee = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    request.setAssignedTo(assignee);
      requestRepository.save(request);
      auditService.log(request, actor, AuditAction.ASSIGNED,
                       "Assigned to " + assignee.getEmail());

    return toDto(request);
    }

@Transactional
    public RequestResponseDto changeStatus(Long requestId, RequestStatus newStatus, String note, User actor) {
      DsarRequest request = findEntity(requestId);
      stateMachine.assertTransition(request.getStatus(), newStatus);

    request.setStatus(newStatus);
      if (newStatus == RequestStatus.COMPLETED) {
        request.setCompletedAt(Instant.now());
      }
      requestRepository.save(request);

    AuditAction action = switch (newStatus) {
      case COMPLETED -> AuditAction.REQUEST_COMPLETED;
      case REJECTED -> AuditAction.REQUEST_REJECTED;
      default -> AuditAction.STATUS_CHANGED;
    };
      auditService.log(request, actor, action, note != null ? note : "Status changed to " + newStatus);

    if (newStatus == RequestStatus.COMPLETED || newStatus == RequestStatus.REJECTED) {
      emailService.sendCompletionNotice(request);
      auditService.log(request, actor, AuditAction.NOTIFICATION_SENT,
                       "Completion notice emailed to " + request.getSubjectEmail());
    }

    return toDto(request);
    }

private DsarRequest findEntity(Long id) {
  return requestRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("DSAR request not found: " + id));
}

private String generateReferenceCode() {
  String code;
  do {
    code = "DSAR-" + (100000 + RANDOM.nextInt(900000));
  } while (requestRepository.existsByReferenceCode(code));
  return code;
}

private RequestResponseDto toDto(DsarRequest r) {
  return new RequestResponseDto(
    r.getId(),
    r.getReferenceCode(),
    r.getSubjectName(),
    r.getSubjectEmail(),
    r.getType(),
    r.getStatus(),
    r.getDescription(),
    r.getAssignedTo() != null ? r.getAssignedTo().getId() : null,
    r.getAssignedTo() != null ? r.getAssignedTo().getFullName() : null,
    r.getReceivedAt(),
    r.getLegalDeadline(),
    r.getCompletedAt(),
    r.isOverdue()
    );
}
  }
