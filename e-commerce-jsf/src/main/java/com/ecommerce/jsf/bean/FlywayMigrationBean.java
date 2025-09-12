package com.ecommerce.jsf.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.flywaydb.core.Flyway;

@Singleton
@Startup
public class FlywayMigrationBean {

    @PostConstruct
    public void migrate() {
        try {
            String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
            String dbUser = System.getenv().getOrDefault("JDBC_USER", "postgres");
            String dbPassword = System.getenv().getOrDefault("JDBC_PASSWORD", "password");
            Flyway flyway = Flyway.configure()
                .dataSource(
                    "jdbc:postgresql://" + dbHost + ":5432/ecommerce",
                    dbUser,
                    dbPassword
                )
                .locations("classpath:db/migration")
                .load();
            flyway.migrate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
