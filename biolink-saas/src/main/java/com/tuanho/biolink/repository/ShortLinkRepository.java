package com.tuanho.biolink.repository;

import com.tuanho.biolink.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
  // Tìm link gốc dựa vào shortCode (Ví dụ: abc1234)
  Optional<ShortLink> findByShortCode(String shortCode);
}