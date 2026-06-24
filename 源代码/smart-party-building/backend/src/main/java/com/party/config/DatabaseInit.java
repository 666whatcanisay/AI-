package com.party.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DatabaseInit {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            // 检查 report_task 表是否存在
            boolean tableExists = checkTableExists("report_task");
            if (!tableExists) {
                System.out.println("Creating report_task table...");
                executeSqlFile("db/update_report_task.sql");
                System.out.println("report_task table created successfully!");
            }
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private boolean checkTableExists(String tableName) {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM " + tableName + " LIMIT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeSqlFile(String filePath) throws Exception {
        ClassPathResource resource = new ClassPathResource(filePath);
        String sql = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        // 分割并执行每个 SQL 语句（以 ; 分隔）
        String[] statements = sql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    System.err.println("Error executing statement: " + e.getMessage());
                }
            }
        }
    }
}
