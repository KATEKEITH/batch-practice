package com.batch2.practice;

import static org.assertj.core.api.Assertions.assertThat;

import com.batch2.practice.config.TestDataSourceConfiguration;
import com.batch2.practice.job.product.BatchJdbcTestConfiguration;
import com.batch2.practice.job.product.SalesSum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@SpringBootTest
@SpringBatchTest // (1)
@EnableBatchProcessing
@ContextConfiguration(classes={
        BatchJdbcTestConfiguration.class,
        TestDataSourceConfiguration.class
})
public class BatchJdbcUnitTestJobConfigurationNewTest {

    @Autowired
    private JdbcPagingItemReader<SalesSum> reader;
    @Autowired private DataSource dataSource;

    private JdbcOperations jdbcTemplate;
    private LocalDate orderDate = LocalDate.of(2019, 10, 6);

    // (4)
    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("orderDate", this.orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    @BeforeEach // (5)
    public void setUp() throws Exception {
//        this.reader.setDataSource(this.dataSource);
        reader.afterPropertiesSet(); // setDataSource 대신 사용
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach // (6)
    public void tearDown() throws Exception {
        this.jdbcTemplate.update("delete from sales");
    }

    @Test
    public void 기간내_Sales가_집계되어_SalesSum이된다() throws Exception {
        //given
        long amount1 = 1000;
        long amount2 = 500;
        long amount3 = 100;

        saveSales(amount1, "1");
        saveSales(amount2, "2");
        saveSales(amount3, "3");

        // SQL 실행 결과 확인
        List<Map<String, Object>> result = jdbcTemplate.queryForList(
                "SELECT order_date, sum(amount) as amount_sum FROM sales GROUP BY order_date"
        );
        System.out.println("SQL Result: " + result);

        // when && then
        SalesSum salesSum = reader.read();
        assertThat(salesSum).isNotNull();
        assertThat(salesSum.getAmountSum()).isEqualTo(amount1 + amount2 + amount3);
        assertThat(reader.read()).isNull();
    }

    private void saveSales(long amount, String orderNo) {
        jdbcTemplate.update("insert into sales (order_date, amount, order_no) values (?, ?, ?)", this.orderDate, amount, orderNo);
    }

}
