package com.tuanho.biolink.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int BASE = ALPHABET.length(); // 62

  // Chuyển ID (số) thành ShortCode (chuỗi)
  // Ví dụ: 1000 -> "qi"
  public String encode(long id) {
    StringBuilder sb = new StringBuilder();
    if (id == 0)
      return String.valueOf(ALPHABET.charAt(0));

    while (id > 0) {
      sb.append(ALPHABET.charAt((int) (id % BASE)));
      id /= BASE;
    }
    return sb.reverse().toString();
  }

  // Chuyển ShortCode (chuỗi) ngược lại thành ID (số)
  // Ví dụ: "qi" -> 1000
  public long decode(String str) {
    long id = 0;
    for (int i = 0; i < str.length(); i++) {
      id = id * BASE + ALPHABET.indexOf(str.charAt(i));
    }
    return id;
  }
}