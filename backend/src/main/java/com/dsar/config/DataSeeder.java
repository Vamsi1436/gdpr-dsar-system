package com.dsar.config;

import com.dsar.domain.Role;
import com.dsar.domain.User;
import com.dsar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
* Seeds a default admin account on first startup so the application is
  * immediately usable in a fresh environment. Change this password
  * immediately in any non-local environment.
  */
@Component
  @RequiredArgsConstructor
  @Slf4j
  public class DataSeeder implements CommandLineRunner {

private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

@Override
    public void run(String... args) {
      if (userRepository.existsByEmail("admin@dsar.local")) {
        return;
      }

    User admin = User.builder()
      .email("admin@dsar.local")
      .password(passwordEncoder.encode("ChangeMe123!"))
      .fullName("Default Administrator")
      .role(Role.ADMIN)
      .enabled(true)
      .build();

    userRepository.save(admin);
      log.info("Seeded default admin account: admin@dsar.local / ChangeMe123! (please change this password)");
    }
  }
