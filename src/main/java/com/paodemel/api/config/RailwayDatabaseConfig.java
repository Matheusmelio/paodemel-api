package com.paodemel.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RailwayDatabaseConfig {

  @Bean
  @Primary
  @ConditionalOnProperty(name = "DATABASE_URL")
  public DataSource railwayDataSource(
      @Value("${DATABASE_URL}") String databaseUrl,
      @Value("${DATABASE_USERNAME:}") String configuredUsername,
      @Value("${DATABASE_PASSWORD:}") String configuredPassword
  ) {
    if (databaseUrl.startsWith("jdbc:")) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(databaseUrl);
      config.setUsername(configuredUsername);
      config.setPassword(configuredPassword);
      config.setDriverClassName("org.postgresql.Driver");
      return new HikariDataSource(config);
    }

    URI dbUri = URI.create(databaseUrl.replace("postgres://", "postgresql://"));
    String username = configuredUsername;
    String password = configuredPassword;

    if ((username == null || username.isBlank()) && dbUri.getUserInfo() != null) {
      String[] userInfo = dbUri.getUserInfo().split(":", 2);
      username = userInfo[0];
      if (userInfo.length > 1) {
        password = userInfo[1];
      }
    }

    String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
    if (dbUri.getQuery() != null && !dbUri.getQuery().isBlank()) {
      jdbcUrl += "?" + dbUri.getQuery();
    }

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName("org.postgresql.Driver");
    return new HikariDataSource(config);
  }
}
