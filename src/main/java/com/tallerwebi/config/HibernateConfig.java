package com.tallerwebi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    String dbHost = System.getenv("DB_HOST");
    String dbPort = System.getenv("DB_PORT");
    String dbName = System.getenv("DB_NAME");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    if (dbHost == null) dbHost = "localhost";
    if (dbPort == null) dbPort = "3306";
    if (dbName == null) dbName = "tallerwebi";
    if (dbUser == null) dbUser = "root";

    if (dbPassword == null) dbPassword = "";

    String dbSsl = System.getenv("DB_SSL");
    if (dbSsl == null) dbSsl = "false";

    String url = String.format(
      "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=America/Buenos_Aires&allowPublicKeyRetrieval=true",
      dbHost,
      dbPort,
      dbName,
      dbSsl
    );

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(dbUser);
    config.setPassword(dbPassword);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    config.setMaximumPoolSize(5); // Límite seguro para el plan Free de Aiven
    config.setMinimumIdle(2);
    config.setIdleTimeout(300000);
    config.setMaxLifetime(600000);

    return new HikariDataSource(config);
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan("com.tallerwebi.dominio");
    sessionFactory.setHibernateProperties(hibernateProperties());
    return sessionFactory;
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    return new HibernateTransactionManager(sessionFactory(dataSource()).getObject());
  }

  private Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    properties.setProperty("hibernate.show_sql", "false");
    properties.setProperty("hibernate.format_sql", "false");
    properties.setProperty("hibernate.default_batch_fetch_size", "20");
    properties.setProperty("hibernate.jdbc.batch_size", "20");
    properties.setProperty("hibernate.hbm2ddl.auto", "update");
    properties.setProperty("hibernate.hbm2ddl.import_files", "");
    properties.setProperty("hibernate.connection.characterEncoding", "utf8");
    properties.setProperty("hibernate.connection.CharSet", "utf8");
    properties.setProperty("hibernate.connection.useUnicode", "true");
    return properties;
  }
}
