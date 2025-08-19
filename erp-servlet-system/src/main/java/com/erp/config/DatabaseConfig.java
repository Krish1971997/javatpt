package com.erp.config;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection pool management
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static DatabaseConfig instance;
    private PoolDataSource poolDataSource;
    
    private DatabaseConfig() {
        initializeConnectionPool();
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void initializeConnectionPool() {
        try {
            Properties props = loadDatabaseProperties();
            
            poolDataSource = PoolDataSourceFactory.getPoolDataSource();
            poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            poolDataSource.setURL(props.getProperty("db.url", "jdbc:oracle:thin:@localhost:1521:XE"));
            poolDataSource.setUser(props.getProperty("db.username", "erp_user"));
            poolDataSource.setPassword(props.getProperty("db.password", "erp_password"));
            
            // Connection pool settings
            poolDataSource.setInitialPoolSize(5);
            poolDataSource.setMinPoolSize(5);
            poolDataSource.setMaxPoolSize(20);
            poolDataSource.setConnectionWaitTimeout(30);
            poolDataSource.setInactiveConnectionTimeout(300);
            poolDataSource.setValidateConnectionOnBorrow(true);
            poolDataSource.setSQLForValidateConnection("SELECT 1 FROM DUAL");
            
            logger.info("Database connection pool initialized successfully");
            
        } catch (SQLException e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Database properties loaded from file");
            } else {
                logger.warn("database.properties not found, using default values");
            }
        } catch (IOException e) {
            logger.warn("Error loading database properties, using defaults", e);
        }
        return props;
    }
    
    public Connection getConnection() throws SQLException {
        if (poolDataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return poolDataSource.getConnection();
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
    
    public void shutdown() {
        if (poolDataSource != null) {
            try {
                // Close all connections in the pool
                logger.info("Shutting down database connection pool");
            } catch (Exception e) {
                logger.error("Error during database shutdown", e);
            }
        }
    }
}