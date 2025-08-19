package com.example.erp.dao;

import com.example.erp.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDao.class);

    private final HikariDataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long createProduct(String name, double price) throws SQLException {
        String call = "{ call PKG_PRODUCT.CREATE_PRODUCT(?, ?, ?) }";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.registerOutParameter(3, java.sql.Types.NUMERIC);
            stmt.execute();
            long id = stmt.getLong(3);
            LOGGER.info("Created product id={} name={} price={}", id, name, price);
            return id;
        }
    }

    public Optional<Product> getProductById(long id) throws SQLException {
        String call = "{ call PKG_PRODUCT.GET_PRODUCT(?, ?) }";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {
            stmt.setLong(1, id);
            stmt.registerOutParameter(2, java.sql.Types.REF_CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs != null && rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateProduct(long id, String name, double price) throws SQLException {
        String call = "{ call PKG_PRODUCT.UPDATE_PRODUCT(?, ?, ?, ?) }";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {
            stmt.setLong(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);
            stmt.execute();
            int updated = stmt.getInt(4);
            LOGGER.info("Updated product id={} rows={}", id, updated);
            return updated > 0;
        }
    }

    public boolean deleteProduct(long id) throws SQLException {
        String call = "{ call PKG_PRODUCT.DELETE_PRODUCT(?, ?) }";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {
            stmt.setLong(1, id);
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);
            stmt.execute();
            int deleted = stmt.getInt(2);
            LOGGER.info("Deleted product id={} rows={}", id, deleted);
            return deleted > 0;
        }
    }

    public List<Product> listProducts() throws SQLException {
        // For simplicity, use a direct SELECT. You can create a PL/SQL that returns a cursor instead.
        String sql = "SELECT ID, NAME, PRICE, CREATED_AT FROM PRODUCTS ORDER BY ID";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("ID");
        String name = rs.getString("NAME");
        double price = rs.getDouble("PRICE");
        Timestamp createdAtTs = rs.getTimestamp("CREATED_AT");
        Instant createdAt = createdAtTs != null ? createdAtTs.toInstant() : null;
        return new Product(id, name, price, createdAt);
    }
}