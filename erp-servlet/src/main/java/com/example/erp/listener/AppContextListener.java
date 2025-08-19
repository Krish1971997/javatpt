package com.example.erp.listener;

import com.example.erp.dao.ProductDao;
import com.example.erp.db.ConnectionPool;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        HikariDataSource dataSource = ConnectionPool.initializeFromEnv();
        context.setAttribute("dataSource", dataSource);
        context.setAttribute("productDao", new ProductDao(dataSource));

        LOGGER.info("Application context initialized. DataSource and DAOs are ready.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Object ds = context.getAttribute("dataSource");
        if (ds instanceof HikariDataSource) {
            try {
                ((HikariDataSource) ds).close();
                LOGGER.info("HikariDataSource closed.");
            } catch (Exception e) {
                LOGGER.warn("Failed to close HikariDataSource", e);
            }
        }
    }
}

