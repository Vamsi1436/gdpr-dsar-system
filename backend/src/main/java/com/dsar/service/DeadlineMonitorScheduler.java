package com.dsar.service;

import com.dsar.domain.DsarRequest;
import com.dsar.domain.RequestStatus;
import com.dsar.domain.User;
import com.dsar.repository.DsarRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
* Runs periodically to flag DSAR requests that are close to, or past,
  * their statutory 30-day deadline and emails the assigned case handler.
  */
@Component
  @RequiredArgsConstructor
  @Slf4j
  public class DeadlineMonitorScheduler {

private static final List<RequestStatus> OPEN_STATUSES =
    List.of(RequestStatus.RECEIVED, RequestStatus.IN_PROGRESS, RequestStatus.PENDING_REVIEW);

private final DsarRequestRepository requestRepository;
    private final EmailService emailService;

@Value("${dsar.deadline.warning-days-before}")
    private int warningDaysBefore;

@Scheduled(cron = "0 0 * * * *")
    public void checkDeadlines() {
      List<DsarRequest> openRequests = requestRepository.findByStatusIn(OPEN_STATUSES);
      Instant now = Instant.now();
      Instant warningThreshold = now.plus(warningDaysBefore, ChronoUnit.DAYS);

    for (DsarRequest request : openRequests) {
      User handler = request.getAssignedTo();
      if (handler == null) {
        continue;
      }
      if (request.getLegalDeadline().isBefore(now)) {
        log.warn("DSAR request {} is OVERDUE (deadline {})", request.getReferenceCode(), request.getLegalDeadline());
        emailService.sendOverdueAlert(request, handler);
      } else if (request.getLegalDeadline().isBefore(warningThreshold)) {
        log.info("DSAR request {} is at risk (deadline {})", request.getReferenceCode(), request.getLegalDeadline());
        emailService.sendDeadlineWarning(request, handler);
      }
    }
    }
  }
