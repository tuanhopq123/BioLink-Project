package com.tuanho.biolink.controller;

import com.tuanho.biolink.dto.ShortenRequest;
import com.tuanho.biolink.entity.User;
import com.tuanho.biolink.service.ShortLinkService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

  private final ShortLinkService service;

  // API 1: Rút gọn link
  @PostMapping("/api/v1/shorten")
  public ResponseEntity<String> shorten(@RequestBody ShortenRequest request, @AuthenticationPrincipal User user) {
    String shortCode = service.shortenUrl(request.getOriginalUrl(), user);
    // Trả về full URL (Ví dụ: http://localhost:8080/abc)
    String fullShortUrl = "http://localhost:8080/" + shortCode;
    return ResponseEntity.ok(fullShortUrl);
  }

  // API 2: Redirect (Chuyển hướng)
  @GetMapping("/{shortCode}")
  public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
    String originalUrl = service.getOriginalUrl(shortCode);

    // Chuyển hướng 302 (Temporary Redirect)
    response.sendRedirect(originalUrl);
  }
}