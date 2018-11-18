package com;

import java.io.IOException;
import java.sql.*;

public class NonRepeatableReads {
    static String userName = "root";
    static String password = "sql123";
    static String connectionUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
             Statement stat = conn.createStatement()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            ResultSet resultSet = stat.executeQuery("SELECT * FROM Books");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }

            new OtherTransaction().start();
            Thread.sleep(2000);

            ResultSet resultSet2 = stat.executeQuery("SELECT * FROM Books");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("name"));
            }

        }
    }

    static class OtherTransaction extends Thread {
        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement stat = conn.createStatement()) {
                conn.setAutoCommit(false);
              conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                stat.executeUpdate("UPDATE Books set name ='new value' where id=1");
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}























