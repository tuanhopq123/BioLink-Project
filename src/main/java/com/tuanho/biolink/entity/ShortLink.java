package com.tuanho.biolink.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "short_links", indexes = {
    @Index(name = "idx_short_code", columnList = "short_code") // Tối ưu tìm kiếm
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLink {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Link gốc (Link dài)
  @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
  private String originalUrl;

  // Mã rút gọn (Ví dụ: abc1234)
  @Column(name = "short_code", unique = true, length = 10)
  private String shortCode;

  // Link này của ai?
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // Thống kê số click (Update Async sau này)
  @Column(name = "click_count")
  private Long clickCount = 0L;

  // Mật khẩu bảo vệ link (Tính năng Pro)
  private String password;

  // Link có đang hoạt động không? (Admin ban link bẩn)
  @Column(name = "is_active")
  private boolean isActive = true;

  // Ngày hết hạn (Tính năng tự hủy)
  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}