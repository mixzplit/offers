package com.tupperware.datasources.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	    basePackages = {
	        "com.tupperware.auth.repository.mariadb", 
	        "com.tupperware.bitacora.repository", 
	        "com.tupperware.wao.repository"
	    },
	    entityManagerFactoryRef = "mariaDbEntityManagerFactory",
	    transactionManagerRef = "mariaDbTransactionManager"
	)
public class MariaDbConfig {

	@Value("${spring.datasource.mariadb.url}")
	private String url;
	@Value("${spring.datasource.mariadb.username}")
	private String username;
	@Value("${spring.datasource.mariadb.password}")
	private String password;
	@Value("${spring.datasource.mariadb.driver-class-name}")
	private String driverClass;
	@Value("${spring.datasource.mariadb.hikari.maximum-pool-size}")
	private Integer maxPoolSize;
	
	@Primary
	@Bean(name = "mariaDbDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.mariadb")
	DataSource mariaDbDataSource() {
//		return DataSourceBuilder.create()
////				.url(url)
////				.username(username)
////				.password(password)
////				.driverClassName(driverClass)
//				.type(HikariDataSource.class)
//				.build();
	       HikariDataSource dataSource = new HikariDataSource();
	        dataSource.setJdbcUrl(url);
	        dataSource.setUsername(username);
	        dataSource.setPassword(password);
	        dataSource.setDriverClassName(driverClass);
	        dataSource.setMaximumPoolSize(maxPoolSize); // Set Hikari max pool size

	        return dataSource;
	}
	
	@Primary
    @Bean(name = "mariaDbEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean mariaDbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mariaDbDataSource())
                .packages(
                    "com.tupperware.auth.entity", 
                    "com.tupperware.bitacora.entity", 
                    "com.tupperware.wao.entity"
                )
                .persistenceUnit("mariaDbPU")
//                .properties(Map.of("hibernate.dialect","org.hibernate.dialect.MariaDBDialect"))
                .build();
    }

	@Primary
    @Bean(name = "mariaDbTransactionManager")
    PlatformTransactionManager mariaDbTransactionManager(
            @Qualifier("mariaDbEntityManagerFactory") LocalContainerEntityManagerFactoryBean mariaDbEntityManagerFactory) {
        return new JpaTransactionManager(mariaDbEntityManagerFactory.getObject());
    }
       
}
