package com.ecommerce.jsf.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class JdbcDriverCleanupListener implements ServletContextListener {
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      if (driver.getClass().getName().equals("org.postgresql.Driver")) {
        try {
          DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
          sce.getServletContext().log("Error deregistering JDBC driver: " + driver, e);
        }
      }
    }
  }
}
