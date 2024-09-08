package com.pmc.personalization.batchsample.config;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

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
public class BatchConfig extends DefaultBatchConfiguration {

    private final DataSource batchMetadataDataSource;
    private final DataSource businessDataSource;

    public BatchConfig(@Qualifier("batchMetadataDataSource") DataSource batchMetadataDataSource,
                       @Qualifier("businessDataSource") DataSource businessDataSource) {
        this.batchMetadataDataSource = batchMetadataDataSource;
        this.businessDataSource = businessDataSource;
    }

    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(
            @Value("${job.name}") String jobName,
            JobLauncher jobLauncher,
            JobExplorer jobExplorer,
            JobRepository jobRepository
    ) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        runner.setJobName(jobName);
        return runner;
    }

    @Bean(name = "businessTransactionManager")
    public DataSourceTransactionManager businessTransactionManager(@Qualifier("businessDataSource") DataSource businessDataSource) {
        return new DataSourceTransactionManager(businessDataSource);
    }

    @Bean
    public DataSourceInitializer metaDataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(batchMetadataDataSource);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("sql/schema-h2.sql"));
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        return dataSourceInitializer;
    }

    @Override
    protected DataSource getDataSource() {
        // Spring Batch 메타데이터를 저장할 DataSource로 H2를 사용
        return batchMetadataDataSource;
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        // Batch 메타데이터와 관련된 트랜잭션을 처리할 TransactionManager
        return new DataSourceTransactionManager(batchMetadataDataSource);
    }

}
