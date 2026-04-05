package at.htl.leonding.vinylmaster.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL", "jdbc:postgresql://localhost:5432/vinylmaster"));
            config.setUsername(dotenv.get("DB_USER", "app"));
            config.setPassword(dotenv.get("DB_PASSWORD", "app"));
            config.setDriverClassName("org.postgresql.Driver");
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
            
            // Run Flyway migrations
            runMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    private static void runMigrations() {
        try {
            org.flywaydb.core.Flyway flyway = org.flywaydb.core.Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
        } catch (Exception e) {
            System.err.println("Warning: Flyway migration failed: " + e.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
