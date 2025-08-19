package com.example.erp.servlet;

import com.example.erp.dao.ProductDao;
import com.example.erp.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductServlet extends HttpServlet {

    private transient ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDao = (ProductDao) getServletContext().getAttribute("productDao");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<Product> products = productDao.listProducts();
                objectMapper.writeValue(resp.getWriter(), products);
            } catch (SQLException e) {
                resp.setStatus(500);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
            }
            return;
        }

        // /{id}
        String[] parts = pathInfo.split("/");
        if (parts.length == 2) {
            try {
                long id = Long.parseLong(parts[1]);
                Optional<Product> product = productDao.getProductById(id);
                if (product.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), product.get());
                } else {
                    resp.setStatus(404);
                    objectMapper.writeValue(resp.getWriter(), Map.of("error", "Not found"));
                }
            } catch (NumberFormatException ex) {
                resp.setStatus(400);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid id"));
            } catch (SQLException e) {
                resp.setStatus(500);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
            }
            return;
        }

        resp.setStatus(404);
        objectMapper.writeValue(resp.getWriter(), Map.of("error", "Not found"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDao = (ProductDao) getServletContext().getAttribute("productDao");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> body = objectMapper.readValue(req.getInputStream(), Map.class);
            String name = (String) body.get("name");
            Number priceNum = (Number) body.get("price");
            if (name == null || name.isBlank() || priceNum == null) {
                resp.setStatus(400);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "name and price are required"));
                return;
            }
            double price = priceNum.doubleValue();
            long id = productDao.createProduct(name, price);
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), Map.of("id", id));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDao = (ProductDao) getServletContext().getAttribute("productDao");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Missing id in path"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length != 2) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid path"));
            return;
        }

        try {
            long id = Long.parseLong(parts[1]);
            Map<String, Object> body = objectMapper.readValue(req.getInputStream(), Map.class);
            String name = (String) body.get("name");
            Number priceNum = (Number) body.get("price");
            if (name == null || name.isBlank() || priceNum == null) {
                resp.setStatus(400);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "name and price are required"));
                return;
            }
            boolean updated = productDao.updateProduct(id, name, priceNum.doubleValue());
            if (updated) {
                objectMapper.writeValue(resp.getWriter(), Map.of("updated", true));
            } else {
                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Not found"));
            }
        } catch (NumberFormatException ex) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid id"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDao = (ProductDao) getServletContext().getAttribute("productDao");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Missing id in path"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length != 2) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid path"));
            return;
        }

        try {
            long id = Long.parseLong(parts[1]);
            boolean deleted = productDao.deleteProduct(id);
            if (deleted) {
                objectMapper.writeValue(resp.getWriter(), Map.of("deleted", true));
            } else {
                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Not found"));
            }
        } catch (NumberFormatException ex) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid id"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
        }
    }
}

