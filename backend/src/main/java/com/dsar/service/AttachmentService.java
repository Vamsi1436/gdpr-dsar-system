package com.dsar.service;

import com.dsar.domain.Attachment;
import com.dsar.domain.AuditAction;
import com.dsar.domain.DsarRequest;
import com.dsar.domain.User;
import com.dsar.exception.ResourceNotFoundException;
import com.dsar.repository.AttachmentRepository;
import com.dsar.repository.DsarRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
  @RequiredArgsConstructor
  public class AttachmentService {

private static final String STORAGE_ROOT = "storage/attachments";

private final AttachmentRepository attachmentRepository;
    private final DsarRequestRepository requestRepository;
    private final AuditService auditService;
    private final RedactionService redactionService;

@Transactional
    public Attachment store(Long requestId, MultipartFile file, boolean redact, User actor) {
      DsarRequest request = requestRepository.findById(requestId)
        .orElseThrow(() -> new ResourceNotFoundException("DSAR request not found: " + requestId));

    try {
      Path dir = Paths.get(STORAGE_ROOT, requestId.toString());
      Files.createDirectories(dir);

      String storedName = UUID.randomUUID() + "-" + file.getOriginalFilename();
      Path target = dir.resolve(storedName);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

      if (redact) {
        redactionService.redact(target);
      }

      Attachment attachment = Attachment.builder()
        .request(request)
        .fileName(file.getOriginalFilename())
        .contentType(file.getContentType())
        .storagePath(target.toString())
        .redacted(redact)
        .uploadedBy(actor.getId())
        .build();

      attachment = attachmentRepository.save(attachment);
      auditService.log(request, actor, AuditAction.ATTACHMENT_UPLOADED,
                       "Uploaded " + file.getOriginalFilename() + (redact ? " (redacted)" : ""));

      return attachment;
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to store attachment", e);
    }
    }

@Transactional(readOnly = true)
    public List<Attachment> listForRequest(Long requestId) {
      return attachmentRepository.findByRequest_Id(requestId);
    }
  }
