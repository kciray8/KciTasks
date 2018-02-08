package com.kciray.play;


import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        try {//flaw 1: checked exceptions
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:test","sa","");

            JdbcDataSource dataSource = new JdbcDataSource();

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE Person(id INTEGER, name VARCHAR)");

            statement.execute("INSERT INTO Person VALUES (0, 'Lily')");
            statement.execute("INSERT INTO Person VALUES (1, 'Robin')");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM Person");
            while (resultSet.next()){
                System.out.println(resultSet.getString("name"));
            }

            try {
                statement.execute("CREATE TALE");
            }catch (SQLException e){//flaw 2: no specific exceptions
                e.printStackTrace();
            }

            //flaw 3: redundant close() statements
            resultSet.close();
            statement.close();
            connection.close();

            System.out.println(connection.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
