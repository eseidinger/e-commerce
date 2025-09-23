package com.ecommerce.jsf.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

@Singleton
@Startup
public class FlywayMigrationBean {

    @Inject
    @ConfigProperty(name = "db.host", defaultValue = "localhost")
    private String dbHost;

    @Inject
    @ConfigProperty(name = "db.user", defaultValue = "postgres")
    private String dbUser;

    @Inject
    @ConfigProperty(name = "db.password", defaultValue = "password")
    private String dbPassword;

    @PostConstruct
    public void migrate() {
        try {
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
