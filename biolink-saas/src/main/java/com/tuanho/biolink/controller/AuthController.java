package com.tuanho.biolink.controller;

import com.tuanho.biolink.dto.LoginRequest;
import com.tuanho.biolink.dto.LoginResponse;
import com.tuanho.biolink.dto.RegisterRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import com.tuanho.biolink.entity.User;
import com.tuanho.biolink.repository.UserRepository;
import com.tuanho.biolink.service.AuthenticationService;
import com.tuanho.biolink.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  // Lấy thời gian hết hạn từ config để set cho Cookie (7 ngày)
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request.getEmail(), request.getPassword()));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody LoginRequest request,
      HttpServletResponse response // Cần biến này để set Cookie
  ) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // --- TẠO HTTP ONLY COOKIE ---
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true) // Quan trọng: JS không đọc được
        .secure(false) // Để false khi chạy localhost (http), lên server thật (https) thì để true
        .path("/") // Cookie có hiệu lực toàn domain
        .maxAge(refreshExpiration / 1000)
        .sameSite("Strict") // Chống CSRF
        .build();

    // Gắn cookie vào phản hồi
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    // Chỉ trả về AccessToken trong Body
    return ResponseEntity.ok(LoginResponse.builder()
        .accessToken(accessToken)
        .build());
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(
      HttpServletRequest request, // Đọc Cookie từ request
      HttpServletResponse response // Để set lại cookie mới (nếu muốn xoay vòng token)
  ) {
    // 1. Tìm Refresh Token trong Cookie thay vì Header
    String refreshToken = null;
    if (request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    if (refreshToken == null) {
      return ResponseEntity.status(403).build(); // Không tìm thấy cookie
    }

    // 2. Các bước xác thực giữ nguyên
    String userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = userRepository.findByEmail(userEmail).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        String accessToken = jwtService.generateToken(user);
        // để thực hiện cơ chế "Refresh Token Rotation" (bảo mật cực cao)

        return ResponseEntity.ok(LoginResponse.builder()
            .accessToken(accessToken)
            .build());
      }
    }
    return ResponseEntity.status(403).build();
  }
}