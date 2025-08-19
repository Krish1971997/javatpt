package com.erp.util;

import com.erp.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Database utility class for common database operations
 */
public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    
    /**
     * Execute a PL/SQL procedure with parameters
     */
    public static CallableStatement prepareCall(Connection connection, String procedureCall) throws SQLException {
        return connection.prepareCall(procedureCall);
    }
    
    /**
     * Close database resources safely
     */
    public static void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        closeResultSet(resultSet);
        closeStatement(statement);
        closeConnection(connection);
    }
    
    public static void closeResources(Connection connection, CallableStatement statement) {
        closeStatement(statement);
        closeConnection(connection);
    }
    
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Error closing ResultSet", e);
            }
        }
    }
    
    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Error closing Statement", e);
            }
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing Connection", e);
            }
        }
    }
    
    /**
     * Handle database transactions
     */
    public static void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
                logger.debug("Transaction committed successfully");
            } catch (SQLException e) {
                logger.error("Error committing transaction", e);
            }
        }
    }
    
    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                logger.debug("Transaction rolled back successfully");
            } catch (SQLException e) {
                logger.error("Error rolling back transaction", e);
            }
        }
    }
    
    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }
}