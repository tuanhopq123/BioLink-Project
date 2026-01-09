package com.tuanho.biolink.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Lombok tự sinh Getter/Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  // Phân quyền: USER hoặc ADMIN
  @Enumerated(EnumType.STRING)
  private Role role;

  // Gói cước: FREE, PRO (Để kiếm tiền sau này)
  @Enumerated(EnumType.STRING)
  @Column(name = "plan_type")
  private PlanType planType;

  // API Key riêng để tích hợp vào tool khác (Tính năng cao cấp)
  @Column(name = "api_key", unique = true)
  private String apiKey;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override // Username chính là Email
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    if (this.role == null)
      this.role = Role.USER;
    if (this.planType == null)
      this.planType = PlanType.FREE;
  }

  public enum Role {
    USER, ADMIN
  }

  public enum PlanType {
    FREE, PRO, ENTERPRISE
  }
}