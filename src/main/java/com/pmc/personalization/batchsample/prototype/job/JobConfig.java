package com.pmc.personalization.batchsample.prototype.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 *
 * <ul>
 *     <li>Updated on: 2024-09-07</li>
 *     <li>Updated by: hskim</li>
 * </ul>
 */
@Configuration
public class JobConfig {
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("sampleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, JdbcBatchItemWriter<String> writer, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(itemReader())
                .writer(writer)
                .build();
    }

    @Bean
    public ListItemReader<String> itemReader() {
        List<String> data = Arrays.asList("item1", "item2", "item3");
        return new ListItemReader<>(data);
    }

    @Bean
    public JdbcBatchItemWriter<String> writer(@Qualifier("businessDataSource") DataSource dataSource) {
        JdbcBatchItemWriter<String> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO my_table (name) VALUES (?)");
        writer.setItemPreparedStatementSetter((item, ps) -> ps.setString(1, item));
        return writer;
    }
}
