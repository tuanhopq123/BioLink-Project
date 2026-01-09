package com.tuanho.biolink.service;

import com.tuanho.biolink.entity.ShortLink;
import com.tuanho.biolink.entity.User;
import com.tuanho.biolink.repository.ShortLinkRepository;
import com.tuanho.biolink.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

  private final ShortLinkRepository repository;
  private final Base62Encoder base62Encoder;

  // Inject thêm Redis
  private final RedisTemplate<String, String> redisTemplate;

  // Hàm tạo link (Giữ nguyên hoặc thêm cache warming nếu muốn)
  @Transactional
  public String shortenUrl(String originalUrl, User user) {
    ShortLink link = ShortLink.builder()
        .originalUrl(originalUrl)
        .user(user)
        .build();
    ShortLink savedLink = repository.save(link);
    String shortCode = base62Encoder.encode(savedLink.getId());
    savedLink.setShortCode(shortCode);
    repository.save(savedLink);

    // (Tùy chọn) Lưu luôn vào Redis ngay lúc tạo để người tạo click thử được ngay
    redisTemplate.opsForValue().set(shortCode, originalUrl, 7, TimeUnit.DAYS);

    return shortCode;
  }

  // Hàm lấy link (Nâng cấp Logic Caching)
  public String getOriginalUrl(String shortCode) {
    // 1. Tìm trong Redis trước
    String cachedUrl = redisTemplate.opsForValue().get(shortCode);

    if (cachedUrl != null) {
      System.out.println("🔥 Cache Hit: Lấy từ Redis (Siêu nhanh)");
      return cachedUrl;
    }

    // 2. Nếu không có (Cache Miss), mới vào DB tìm
    System.out.println("🐢 Cache Miss: Phải vào DB tìm");
    ShortLink link = repository.findByShortCode(shortCode)
        .orElseThrow(() -> new RuntimeException("Link not found"));

    // 3. Lưu ngược lại vào Redis để lần sau dùng (Hết hạn sau 7 ngày)
    redisTemplate.opsForValue().set(shortCode, link.getOriginalUrl(), 7, TimeUnit.DAYS);

    return link.getOriginalUrl();
  }
}