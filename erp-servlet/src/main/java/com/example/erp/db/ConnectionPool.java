package com.example.erp.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectionPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    private ConnectionPool() {}

    public static HikariDataSource initializeFromEnv() {
        String jdbcUrl = getEnvOrDefault("ORACLE_JDBC_URL", "jdbc:oracle:thin:@//localhost:1521/ORCLCDB");
        String username = getEnvOrDefault("ORACLE_DB_USER", "system");
        String password = getEnvOrDefault("ORACLE_DB_PASSWORD", "oracle");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.setDriverClassName("oracle.jdbc.OracleDriver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(15000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Optional Oracle-specific performance properties
        config.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", "10000");
        config.addDataSourceProperty("oracle.jdbc.fanEnabled", "false");

        LOGGER.info("Initializing HikariCP with URL: {} and user: {}", jdbcUrl, username);
        return new HikariDataSource(config);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}

