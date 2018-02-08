package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
public class Main implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    @Autowired
    MainUtils mainUtils;

    @Autowired
    DataSource dataSource;

    @Bean
    PlatformTransactionManager tm(){
        MyTransactionManager transactionManager = new MyTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    public void run(String... strings) throws Exception {
        mainUtils.check();
        //mainUtils.mandatoryTransaction();//IllegalTransactionStateException:
        //No existing transaction found for transaction marked with propagation 'mandatory'
    }
}
