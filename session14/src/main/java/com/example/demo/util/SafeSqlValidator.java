package com.example.demo.util;

import java.util.List;

// Logic kiểm duyệt SQL an toàn (Safe SQL Validator)
public class SafeSqlValidator {
    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE", "GRANT", "REVOKE"
    );

    public static String validateAndSanitize(String sql) {
        String cleanSql = sql.trim();
        String upper = cleanSql.toUpperCase();

        // 1. Chỉ cho phép lệnh SELECT
        if (!upper.startsWith("SELECT")) {
            throw new SecurityException("Vi phạm an toàn: Chỉ cho phép thực thi câu lệnh SELECT.");
        }

        // 2. Chặn từ khóa nguy hiểm
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (upper.matches(".*\\b" + keyword + "\\b.*")) {
                throw new SecurityException("Từ chối thực thi: Phát hiện câu lệnh nguy hiểm chứa từ khóa " + keyword);
            }
        }

        // 3. Tự động ép LIMIT 100 nếu chưa có
        if (!upper.contains("LIMIT")) {
            cleanSql += " LIMIT 100";
        }
        return cleanSql;
    }
}
