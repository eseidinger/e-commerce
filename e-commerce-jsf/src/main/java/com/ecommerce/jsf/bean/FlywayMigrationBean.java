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
            String dbHost = System.getenv("DB_HOST");
            String dbUser = System.getenv("JDBC_USER");
            String dbPassword = System.getenv("JDBC_PASSWORD");
            if (dbPassword == null) dbPassword = "password";
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
