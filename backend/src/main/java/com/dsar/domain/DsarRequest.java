package com.dsar.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
  @Table(name = "dsar_requests")
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class DsarRequest {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(name = "reference_code", nullable = false, unique = true)
    private String referenceCode;

@Column(name = "subject_name", nullable = false)
    private String subjectName;

@Column(name = "subject_email", nullable = false)
    private String subjectEmail;

@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;

@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

@Column(columnDefinition = "text")
    private String description;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

@Column(name = "received_at", nullable = false)
    private Instant receivedAt;

@Column(name = "legal_deadline", nullable = false)
    private Instant legalDeadline;

@Column(name = "completed_at")
    private Instant completedAt;

@Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

@Column(name = "updated_at")
    private Instant updatedAt;

@PrePersist
    public void prePersist() {
      Instant now = Instant.now();
      if (createdAt == null) {
        createdAt = now;
      }
      if (receivedAt == null) {
        receivedAt = now;
      }
      if (legalDeadline == null) {
        legalDeadline = receivedAt.plus(30, ChronoUnit.DAYS);
      }
      updatedAt = now;
    }

@PreUpdate
    public void preUpdate() {
      updatedAt = Instant.now();
    }

public boolean isOverdue() {
  return status != RequestStatus.COMPLETED
    && status != RequestStatus.REJECTED
    && Instant.now().isAfter(legalDeadline);
}
  }
