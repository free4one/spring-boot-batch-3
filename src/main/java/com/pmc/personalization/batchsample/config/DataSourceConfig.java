package com.pmc.personalization.batchsample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * <p></p>
 *
 * <ul>
 *     <li>Updated on: 2024-09-07</li>
 *     <li>Updated by: hskim</li>
 * </ul>
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "batchMetadataDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.batch-metadata")
    public DataSource batchMetaDataDataSource() {
        return new DriverManagerDataSource();
    }

    @Primary
    @Bean(name = "businessDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.business")
    public DataSource businessDataSource() {
        return new DriverManagerDataSource();
    }
}
