package com.yazhini.aisqlassistant.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> executeQuery(String sql) {

        // Remove leading/trailing spaces
        String query = sql.trim().toLowerCase();

        // Allow only SELECT statements
        if (!query.startsWith("select")) {
            throw new RuntimeException("Only SELECT queries are allowed.");
        }

        // Execute the SQL query
        return jdbcTemplate.queryForList(sql);
    }
}