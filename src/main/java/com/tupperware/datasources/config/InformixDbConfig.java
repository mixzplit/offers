package com.tupperware.datasources.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "com.tupperware.auth.repository.informix" // Solo los repositorios que necesitan Informix
    },
    entityManagerFactoryRef = "informixEntityManagerFactory",
    transactionManagerRef = "informixTransactionManager"
)
public class InformixDbConfig {
	@Value("${spring.datasource.informix.url}")
	private String url;
	@Value("${spring.datasource.informix.username}")
	private String username;
	@Value("${spring.datasource.informix.password}")
	private String password;
	@Value("${spring.datasource.informix.driver-class-name}")
	private String driverClass;
	
	@Bean(name = "informixDbDataSource")
	DataSource informixDbDataSource() {
		return DataSourceBuilder.create()
				.url(url)
				.username(username)
				.password(password)
				.driverClassName(driverClass)
				.build();
	}
	
	@Bean(name = "informixEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean informixEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(informixDbDataSource())
                .packages("com.tupperware.auth.entity") // Solo las entidades necesarias para Informix
                .persistenceUnit("informixPU")
                .properties(Map.of("hibernate.dialect","org.hibernate.community.dialect.InformixDialect"))
                .build();
    }

    @Bean(name = "informixTransactionManager")
    PlatformTransactionManager informixTransactionManager(
            @Qualifier("informixEntityManagerFactory") LocalContainerEntityManagerFactoryBean informixEntityManagerFactory) {
        return new JpaTransactionManager(informixEntityManagerFactory.getObject());
    }
	
	
}
