package com;

import java.io.IOException;
import java.sql.*;

public class DirtyReads {
    static String userName = "root";
    static String password = "sql123";
    static String connectionUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
             Statement stat = conn.createStatement()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED);
            stat.execute("update Books set name = 'new value' where id=1");
            Thread.sleep(2000);
            new OtherTransaction().start();
            conn.rollback();

        }
    }

    static class OtherTransaction extends Thread {
        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement stat = conn.createStatement()) {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED);
                ResultSet resultSet=stat.executeQuery("SELECT * FROM Books");
                while (resultSet.next()){
                    System.out.println(resultSet.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}























