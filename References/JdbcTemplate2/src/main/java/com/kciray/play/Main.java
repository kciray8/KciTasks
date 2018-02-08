package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ComponentScan
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        context.getBean(Main.class).run();
    }

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.DERBY).build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Value("schema.sql")
    Resource schema;

    @Value("test-data.sql")
    Resource testData;

    void run(){
        String url = jdbcTemplate.execute((ConnectionCallback<String>) con -> con.getMetaData().getURL());
        System.out.println(url);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schema);
        populator.addScript(testData);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDatabasePopulator(populator);
        DatabasePopulatorUtils.execute(populator, dataSource);

        List<String> productNames = jdbcTemplate.queryForList("SELECT name FROM Product", String.class);
        System.out.println(productNames);

        List<Product> products = jdbcTemplate.query("SELECT * FROM Product", (RowMapper<Product>) (rs, rowNum) -> {
            Product product = new Product();
            product.name = rs.getString("name");
            product.price = rs.getDouble("price");

            return product;
        });
        System.out.println(products);

        ProductSet productSet = jdbcTemplate.query("SELECT * FROM Product", (ResultSetExtractor<ProductSet>) rs->{
            ProductSet set = new ProductSet();
            while (rs.next()){
                Product product = new Product();
                product.name = rs.getString("name");
                product.price = rs.getDouble("price");
                set.products.add(product);
            };

            return set;
        });

        System.out.println(productSet.products);
    }
}
