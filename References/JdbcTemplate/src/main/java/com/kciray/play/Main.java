package com.kciray.play;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        DataSource dataSource = context.getBean(DataSource.class);

        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute("CREATE TABLE Customer(id INTEGER, name VARCHAR(100))");

        template.execute((ConnectionCallback<Object>) con -> {
            System.out.println(con.getMetaData().getURL());
            System.out.println(con.getMetaData().getUserName());
            //Not necessary to handle SQLException and close the connection

            return null;
        });

        template.update("INSERT INTO Customer VALUES (1, 'Laura')");

        Boolean ok = template.execute((StatementCallback<Boolean>) stmt -> {
            stmt.execute("INSERT INTO Customer VALUES (2, 'Jack')");
            //Statement is from "java.sql" package
            return Boolean.TRUE;
        });

        System.out.println("ok = " + ok);

        template.execute((PreparedStatementCreator) con -> con.prepareStatement("INSERT INTO Customer VALUES (?,?)"),
                (PreparedStatementCallback<Object>) ps -> {
                    ps.setInt(1, 3);
                    ps.setString(2, "Alex");
                    ps.execute();

                    return null;
                });

        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
        Map<String, Object> params = new HashMap<>();
        params.put("id", 4);
        params.put("name", "Nikola");
        namedTemplate.update("INSERT INTO Customer VALUES (:id,:name)", params);


        template.query("SELECT * FROM Customer", (RowCallbackHandler) rs -> {
            System.out.println(rs.getString("name"));
        });

        Integer count = template.queryForObject("SELECT COUNT (*) FROM Customer", Integer.class);
        System.out.println("count - " + count);

        Map<String, Object> customerMap = template.queryForMap("SELECT * FROM Customer WHERE id = 1");
        System.out.println(customerMap.get("name"));

    }
}
