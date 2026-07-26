package com.dsar.web;

import com.dsar.domain.Role;
import com.dsar.domain.User;
import com.dsar.dto.AuthDtos.LoginRequest;
import com.dsar.dto.AuthDtos.LoginResponse;
import com.dsar.dto.AuthDtos.RegisterRequest;
import com.dsar.repository.UserRepository;
import com.dsar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
  @RequestMapping("/api/auth")
  @RequiredArgsConstructor
  public class AuthController {

private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

@PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    User user = userRepository.findByEmail(request.email())
      .orElseThrow(() -> new IllegalStateException("User vanished after authentication"));

    String token = jwtService.generateToken(springUserDetails(user));
      return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

@PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
      if (userRepository.existsByEmail(request.email())) {
        throw new IllegalArgumentException("An account with that email already exists");
      }

    Role role = Role.valueOf(request.role());
      User user = User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .fullName(request.fullName())
        .role(role)
        .enabled(true)
        .build();
      user = userRepository.save(user);

    String token = jwtService.generateToken(springUserDetails(user));
      return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

private UserDetails springUserDetails(User user) {
  return org.springframework.security.core.userdetails.User
    .withUsername(user.getEmail())
    .password(user.getPassword())
    .authorities("ROLE_" + user.getRole().name())
    .build();
}
  }
