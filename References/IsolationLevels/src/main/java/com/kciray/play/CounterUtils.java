package com.kciray.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class CounterUtils {
    @Transactional
    public void init(){
        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            System.out.println("default isolation is " + connection.getTransactionIsolation());
            //2 = Connection.TRANSACTION_READ_COMMITTED
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jdbcTemplate.execute("CREATE TABLE Counter(num int)");
        jdbcTemplate.update("INSERT INTO Counter(num) values (5)");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CounterUtils self;//CounterUtils$$EnhancerBySpringCGLIB

    @Transactional
    public void dirtyReads(){
        jdbcTemplate.update("UPDATE Counter SET num = 10");
        System.out.println("value was updated to 10");

        self.readInAnotherTransaction();
    }

    //Change to READ_UNCOMMITTED/READ_COMMITTED to see the difference
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void readInAnotherTransaction(){
        int num = jdbcTemplate.queryForObject("SELECT num FROM Counter", Integer.class);
        System.out.println("dirty read " + num);//dirty read 10 OR CannotAcquireLockException
    }

    //Change between READ_COMMITTED/REPEATABLE_READ to see the difference
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void repeatableReads(){
        int num = jdbcTemplate.queryForObject("SELECT num from Counter", Integer.class);
        System.out.println("before: " + num);

        self.changeFromAnotherTransaction();

        num = jdbcTemplate.queryForObject("SELECT num from Counter", Integer.class);
        System.out.println("after: " + num);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeFromAnotherTransaction(){
        System.out.println("value was updated to 20");
        jdbcTemplate.update("UPDATE Counter SET num = 20");
    }

    //Change between REPEATABLE_READ/SERIALIZABLE to see the difference
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void phantomReads(){
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) from Counter", Integer.class);
        System.out.println("counter before is " + count);

        self.insertFromAnotherTransaction();

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) from Counter", Integer.class);
        System.out.println("counter after is " + count);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertFromAnotherTransaction(){
        jdbcTemplate.update("INSERT INTO Counter(num) VALUES (80)");
        System.out.println("a new row was inserted");
    }
}