package com.dsar.workflow;

import com.dsar.domain.RequestStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
* Encodes the legal status transitions for a DSAR request:
* RECEIVED -> IN_PROGRESS -> PENDING_REVIEW -> COMPLETED, with REJECTED
  * reachable from any non-terminal state. Any transition not explicitly
  * listed below is rejected, so the workflow cannot be short-circuited
  * (e.g. RECEIVED straight to COMPLETED).
  */
@Component
  public class RequestStateMachine {

private static final Map<RequestStatus, Set<RequestStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(RequestStatus.class);

static {
  ALLOWED_TRANSITIONS.put(RequestStatus.RECEIVED, EnumSet.of(RequestStatus.IN_PROGRESS, RequestStatus.REJECTED));
  ALLOWED_TRANSITIONS.put(RequestStatus.IN_PROGRESS, EnumSet.of(RequestStatus.PENDING_REVIEW, RequestStatus.REJECTED));
  ALLOWED_TRANSITIONS.put(RequestStatus.PENDING_REVIEW, EnumSet.of(RequestStatus.IN_PROGRESS, RequestStatus.COMPLETED, RequestStatus.REJECTED));
  ALLOWED_TRANSITIONS.put(RequestStatus.COMPLETED, EnumSet.noneOf(RequestStatus.class));
  ALLOWED_TRANSITIONS.put(RequestStatus.REJECTED, EnumSet.noneOf(RequestStatus.class));
}

public boolean canTransition(RequestStatus from, RequestStatus to) {
  return ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
}

public void assertTransition(RequestStatus from, RequestStatus to) {
  if (!canTransition(from, to)) {
    throw new InvalidTransitionException(
      "Cannot transition DSAR request from " + from + " to " + to);
  }
}
  }
