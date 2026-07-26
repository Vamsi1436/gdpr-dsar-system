package com.dsar.web;

import com.dsar.domain.Attachment;
import com.dsar.domain.User;
import com.dsar.repository.UserRepository;
import com.dsar.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
  @RequestMapping("/api/attachments")
  @RequiredArgsConstructor
  public class AttachmentController {

private final AttachmentService attachmentService;
    private final UserRepository userRepository;

@PostMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASE_HANDLER')")
    public Attachment upload(@PathVariable Long requestId,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam(value = "redact", defaultValue = "false") boolean redact,
                             Authentication auth) {
      return attachmentService.store(requestId, file, redact, currentUser(auth));
    }

@GetMapping("/{requestId}")
    public List<Attachment> list(@PathVariable Long requestId) {
      return attachmentService.listForRequest(requestId);
    }

private User currentUser(Authentication auth) {
  String email = auth.getName();
  return userRepository.findByEmail(email)
    .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
}
  }
