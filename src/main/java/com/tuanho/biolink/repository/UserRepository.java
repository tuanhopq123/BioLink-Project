package com.tuanho.biolink.repository;

import com.tuanho.biolink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  // Tìm user theo email để đăng nhập
  Optional<User> findByEmail(String email);

  // Kiểm tra email đã tồn tại chưa (lúc đăng ký)
  boolean existsByEmail(String email);
}