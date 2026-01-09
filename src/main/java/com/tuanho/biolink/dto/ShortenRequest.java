package com.tuanho.biolink.dto;

import lombok.Data;

@Data
public class ShortenRequest {
  private String originalUrl; // Link người dùng muốn rút gọn
}