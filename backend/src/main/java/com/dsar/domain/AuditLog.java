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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
* Immutable audit trail entry. Rows in this table are only ever inserted,
  * never updated or deleted, satisfying the GDPR requirement for a
  * tamper-evident record of every action taken on a request.
  */
@Entity
  @Table(name = "audit_log")
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class AuditLog {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DsarRequest request;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

@Column(columnDefinition = "text")
    private String details;

@Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

@PrePersist
    public void prePersist() {
      if (createdAt == null) {
        createdAt = Instant.now();
      }
    }
  }
