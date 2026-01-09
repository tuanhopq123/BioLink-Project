package com.tuanho.biolink.service;

import com.tuanho.biolink.entity.User;
import com.tuanho.biolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  // Mã hóa mật khẩu (Không bao giờ lưu password thô vào DB)
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public User register(String email, String password) {
    // 1. Check xem email tồn tại chưa
    if (userRepository.existsByEmail(email)) {
      throw new RuntimeException("Email already taken!");
    }

    // 2. Tạo User mới
    User user = User.builder()
        .email(email)
        .password(passwordEncoder.encode(password)) // Mã hóa pass
        .role(User.Role.USER)
        .planType(User.PlanType.FREE)
        .build();

    return userRepository.save(user);
  }
}