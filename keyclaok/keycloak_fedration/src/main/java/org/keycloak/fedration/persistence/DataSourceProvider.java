package org.keycloak.fedration.persistence;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JBossLog
public class DataSourceProvider implements Closeable {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
    private              ExecutorService  executor           = Executors.newFixedThreadPool(1);
    private              HikariDataSource hikariDataSource;
    private static HikariDataSource dataSource;

    public DataSourceProvider() {
    }


    synchronized Optional<DataSource> getDataSource() {
        return Optional.ofNullable(hikariDataSource);
    }


    public void configure(String url, RDBMS rdbms, String user, String email, String pass, String name) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.setPoolName(StringUtils.capitalize("SINGULAR-USER-PROVIDER-" + name + SIMPLE_DATE_FORMAT.format(new Date())));
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setConnectionTestQuery(rdbms.getTestString());
        hikariConfig.setDriverClassName(rdbms.getDriver());
        HikariDataSource newDS = new HikariDataSource(hikariConfig);
        newDS.validate();
        HikariDataSource old = this.hikariDataSource;
        this.hikariDataSource = newDS;
        disposeOldDataSource(old);
    }

    private void disposeOldDataSource(HikariDataSource old) {
        executor.submit(() -> {
            try {
                if (old != null) {
                    old.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void close() {
        executor.shutdownNow();
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    public static synchronized DataSource getInstance() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:3308/user_service_db");
            config.setUsername("postgres");
            config.setPassword("root123");
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
