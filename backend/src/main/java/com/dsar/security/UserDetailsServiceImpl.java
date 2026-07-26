package com.dsar.security;

import com.dsar.domain.User;
import com.dsar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
  @RequiredArgsConstructor
  public class UserDetailsServiceImpl implements UserDetailsService {

private final UserRepository userRepository;

@Override
    public UserDetails loadUserByUsername(String email) {
      User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No user found with email " + email));

    return org.springframework.security.core.userdetails.User
      .withUsername(user.getEmail())
      .password(user.getPassword())
      .disabled(!user.isEnabled())
      .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
      .build();
    }
  }
