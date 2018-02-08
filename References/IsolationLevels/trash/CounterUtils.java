package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

@Component
public class CounterUtils {

    @Transactional
    public void init(){
        /*System.out.println(counterRepository);
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
        //OR NoTransactionException (in case the method doesn't have @Transactional)

        Counter counter = new Counter();
        counter.setValue(454);
        getCounterRepository().save(counter);*/

        jdbcTemplate.execute("drop table c");
        jdbcTemplate.execute("Create table c(id bigint,num int)");
        jdbcTemplate.update("Insert into c values (0,4)");

        System.out.println("created!!!!");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;


    //Create and roll to savepoint

    //@Async
    //@Scheduled(fixedRate = 1000)
    @Transactional
    public void increment(){
        System.out.println("dirtyReads..." + Thread.currentThread());

        int i = 0;
        //Counter counter = getCounterRepository().findById(1L).get();

        System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());


        //for(;;) {
            DefaultTransactionStatus defaultTransactionStatus = (DefaultTransactionStatus)TransactionAspectSupport.currentTransactionStatus();
            System.out.println(defaultTransactionStatus.getSuspendedResources());
            System.out.println(defaultTransactionStatus.isDebug());
            System.out.println(defaultTransactionStatus.getTransaction());

            /*Counter counter = new Counter();
            counter.value++;
            System.out.println(getCounterRepository());
            getCounterRepository().saveAndFlush(counter);*/

            /*Counter counter = new Counter();
            counter.id = 1;
            counter.value = i++;
            getCounterRepository().saveAndFlush(counter);*/

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("update c set num = 100");
            System.out.println("uuu");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        //}
    }





    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    //@Scheduled(fixedRate = 1000)
    //@Async
    public void printAll(){
        System.out.println("Print all..." + Thread.currentThread());
        do{
            /*for (Counter counter : getCounterRepository().findAll()) {
                //System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
                System.out.println("read " + counter);
            }*/

            //System.out.println("read2 " + getCounterRepository().findById(1L).get());
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            int num = jdbcTemplate.queryForObject("select num from c", Integer.class);
            System.out.println("read " + num);

            /*try {
                //System.out.println(jdbcTemplate.getDataSource().getConnection().getTransactionIsolation());
            } catch (SQLException e) {
                e.printStackTrace();
            }*/

            try {
                //wait();
                //Thread.currentThread().wait();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (true);
    }

    public void repeatableRead(){
        int num = jdbcTemplate.queryForObject("select num from c", Integer.class);
        System.out.println("Number was obtained: " + num);
        jdbcTemplate.update("update c set num = 1000");
    }

    public void read(){

    }
}
