package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@ComponentScan
public class Main{
    public static void main(String[] args){
        System.getProperties().put("derby.locks.waitTimeout", "1");
        //Derby can't retrieve properties from application.properties for some reasons
        //And @Transaction/TransactionManager timeouts don't work either

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        context.getBean(Main.class).run();
    }

    @Bean
    DataSource dataSource(){
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.DERBY).build();
    }

    @Bean
    JdbcTemplate getJdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager getTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Autowired
    CounterUtils counterUtils;

    void run(){
        counterUtils.init();
        try {
            counterUtils.dirtyReads();
        }catch (CannotAcquireLockException exception){
            System.out.println("---Can't acquire lock for dirtyReads!---");
        }
        try {
            counterUtils.repeatableReads();
        }catch (CannotAcquireLockException exception){
            System.out.println("---Can't acquire lock for repeatableReads!---");
        }
        try {
            counterUtils.phantomReads();
        }catch (CannotAcquireLockException exception){
            System.out.println("---Can't acquire lock for phantomReads!---");
        }
        System.exit(0);
    }
}

