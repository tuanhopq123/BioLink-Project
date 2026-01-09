package com.tuanho.biolink.controller;

import com.tuanho.biolink.entity.User;
import com.tuanho.biolink.service.AuthenticationService;
import lombok.Data;
import com.tuanho.biolink.service.JwtService;
import com.tuanho.biolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request.getEmail(), request.getPassword()));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // Bước 1: Xác thực (Nếu sai pass nó tự ném lỗi 403)
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));

    // Bước 2: Tìm user trong DB
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow();

    // Bước 3: Sinh Token trả về
    String jwtToken = jwtService.generateToken(user);

    return ResponseEntity.ok(new LoginResponse(jwtToken));
  }

  // Class DTO nhận dữ liệu Login
  @Data
  public static class LoginRequest {
    private String email;
    private String password;
  }

  // Class DTO trả về Token
  @Data
  public static class LoginResponse {
    private String token;

    public LoginResponse(String token) {
      this.token = token;
    }
  }

  // Class DTO đăng ký
  @Data
  public static class RegisterRequest {
    private String email;
    private String password;
  }
}