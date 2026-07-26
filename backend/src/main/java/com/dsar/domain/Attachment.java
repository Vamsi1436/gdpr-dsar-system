package com.dsar.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
  @Table(name = "attachments")
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class Attachment {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DsarRequest request;

@Column(name = "file_name", nullable = false)
    private String fileName;

@Column(name = "content_type")
    private String contentType;

@Column(name = "storage_path", nullable = false)
    private String storagePath;

@Builder.Default
    @Column(nullable = false)
    private boolean redacted = false;

@Column(name = "uploaded_by")
    private Long uploadedBy;

@Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

@PrePersist
    public void prePersist() {
      if (uploadedAt == null) {
        uploadedAt = Instant.now();
      }
    }
  }
