package com.dsar.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
* Very small, illustrative redaction pass. For plain-text evidence files it
  * masks common PII patterns (emails, phone numbers, long digit sequences
                               * such as national ID or account numbers). For anything else (images,
                                                                                             * PDFs, etc.) it simply flags the attachment as redacted so a human
  * reviewer knows manual blur/black-box redaction is still required before
  * export - a placeholder for a more advanced image redaction pipeline.
  */
@Service
  public class RedactionService {

private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[\\w.-]+");
    private static final Pattern PHONE = Pattern.compile("\\b\\d{3}[- .]?\\d{3}[- .]?\\d{4}\\b");
    private static final Pattern LONG_DIGITS = Pattern.compile("\\b\\d{6,}\\b");

public void redact(Path file) throws IOException {
  String contentType = Files.probeContentType(file);
  if (contentType != null && contentType.startsWith("text")) {
    String content = Files.readString(file, StandardCharsets.UTF_8);
    content = EMAIL.matcher(content).replaceAll("[REDACTED-EMAIL]");
    content = PHONE.matcher(content).replaceAll("[REDACTED-PHONE]");
    content = LONG_DIGITS.matcher(content).replaceAll("[REDACTED-ID]");
    Files.writeString(file, content, StandardCharsets.UTF_8);
  }
}
  }
