package com.dsar.dto;

public class AuthDtos {

public record LoginRequest(String email, String password) {}

public record LoginResponse(String token, String email, String fullName, String role) {}

public record RegisterRequest(String email, String password, String fullName, String role) {}
}
