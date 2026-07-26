package com.dsar.repository;

import com.dsar.domain.DsarRequest;
import com.dsar.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface DsarRequestRepository extends JpaRepository<DsarRequest, Long> {

List<DsarRequest> findByStatusIn(List<RequestStatus> statuses);

List<DsarRequest> findByAssignedTo_Id(Long userId);

List<DsarRequest> findByLegalDeadlineBeforeAndStatusNotIn(Instant deadline, List<RequestStatus> excludedStatuses);

boolean existsByReferenceCode(String referenceCode);
}
