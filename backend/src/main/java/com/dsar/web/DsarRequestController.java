package com.dsar.web;

import com.dsar.domain.User;
import com.dsar.dto.DsarRequestDtos.AssignDto;
import com.dsar.dto.DsarRequestDtos.CreateRequestDto;
import com.dsar.dto.DsarRequestDtos.RequestResponseDto;
import com.dsar.dto.DsarRequestDtos.StatusChangeDto;
import com.dsar.repository.UserRepository;
import com.dsar.service.DsarRequestService;
import com.dsar.service.LetterDraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
  @RequestMapping("/api/requests")
  @RequiredArgsConstructor
  public class DsarRequestController {

private final DsarRequestService requestService;
    private final UserRepository userRepository;
    private final LetterDraftService letterDraftService;

@PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RequestResponseDto create(@RequestBody CreateRequestDto dto, Authentication auth) {
      return requestService.createRequest(dto, currentUser(auth));
    }

@GetMapping
    public List<RequestResponseDto> list() {
      return requestService.listAll();
    }

@GetMapping("/{id}")
    public RequestResponseDto get(@PathVariable Long id) {
      return requestService.getById(id);
    }

@PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public RequestResponseDto assign(@PathVariable Long id, @RequestBody AssignDto dto, Authentication auth) {
      return requestService.assign(id, dto.userId(), currentUser(auth));
    }

@PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASE_HANDLER')")
    public RequestResponseDto changeStatus(@PathVariable Long id, @RequestBody StatusChangeDto dto, Authentication auth) {
      return requestService.changeStatus(id, dto.newStatus(), dto.note(), currentUser(auth));
    }

@GetMapping("/{id}/draft-letter")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASE_HANDLER')")
    public String draftLetter(@PathVariable Long id) {
      return letterDraftService.draftLetter(requestService.getEntityById(id));
    }

private User currentUser(Authentication auth) {
  String email = auth.getName();
  return userRepository.findByEmail(email)
    .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
}
  }
