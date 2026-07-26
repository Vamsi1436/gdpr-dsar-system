package com.dsar.service;

import com.dsar.domain.DsarRequest;
import com.dsar.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
  @RequiredArgsConstructor
  @Slf4j
  public class EmailService {

private final JavaMailSender mailSender;

public void sendDeadlineWarning(DsarRequest request, User recipient) {
  send(recipient.getEmail(),
       "[DSAR] Deadline approaching: " + request.getReferenceCode(),
       "Request " + request.getReferenceCode() + " for " + request.getSubjectEmail()
       + " is due by " + request.getLegalDeadline() + ". Please action it promptly.");
}

public void sendOverdueAlert(DsarRequest request, User recipient) {
  send(recipient.getEmail(),
       "[DSAR] OVERDUE: " + request.getReferenceCode(),
       "Request " + request.getReferenceCode() + " for " + request.getSubjectEmail()
       + " passed its legal deadline of " + request.getLegalDeadline() + ".");
}

public void sendCompletionNotice(DsarRequest request) {
  send(request.getSubjectEmail(),
       "Your data request " + request.getReferenceCode() + " has been processed",
       "Your GDPR subject access request has reached status " + request.getStatus() + ".");
}

private void send(String to, String subject, String body) {
  try {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  } catch (Exception e) {
    log.warn("Failed to send email to {}: {}", to, e.getMessage());
  }
}
  }
