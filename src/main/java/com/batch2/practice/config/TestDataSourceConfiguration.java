package com.batch2.practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TestDataSourceConfiguration {

    // (1)
    private static final String CREATE_SQL =
            "create table IF NOT EXISTS sales (id bigint not null auto_increment, amount bigint not null, order_date date, order_no varchar(255), primary key (id))";

    // (2)
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseFactory databaseFactory = new EmbeddedDatabaseFactory();
        databaseFactory.setDatabaseType(EmbeddedDatabaseType.H2);
        return databaseFactory.getDatabase();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // (3)
    @Bean
    public DataSourceInitializer initializer(DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);

        Resource create = new ByteArrayResource(CREATE_SQL.getBytes());
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(create));

        return dataSourceInitializer;
    }

}
