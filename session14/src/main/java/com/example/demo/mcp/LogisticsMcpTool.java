package com.example.demo.mcp;

import com.example.demo.util.SafeSqlValidator;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LogisticsMcpTool {

    private final JdbcTemplate jdbcTemplate;

    public LogisticsMcpTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "Thực thi câu lệnh SQL SELECT để tra cứu số liệu đối soát từ bảng deliveries và incidents.")
    public Map<String, Object> executeSafeQuery(@ToolParam(description = "Câu lệnh SQL SELECT thuần túy") String sqlQuery) {
        try {
            // GỌI VALIDATE TẠI ĐÂY
            String sanitizedSql = SafeSqlValidator.validateAndSanitize(sqlQuery);

            // Chạy câu lệnh đã qua thẩm định
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sanitizedSql);

            return Map.of("status", "SUCCESS", "count", rows.size(), "executedSql", sanitizedSql, "data", rows);
        } catch (SecurityException e) {
            // Bắt lỗi khi AI cố tình gửi lệnh cấm (DROP, DELETE,...)
            return Map.of("status", "SECURITY_BLOCKED", "error", e.getMessage());
        } catch (Exception e) {
            return Map.of("status", "ERROR", "error", e.getMessage());
        }
    }
}