package com;

import java.io.IOException;
import java.sql.*;

public class PhantomReads {
    static String userName = "root";
    static String password = "sql123";
    static String connectionUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
             Statement stat = conn.createStatement()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            ResultSet resultSet = stat.executeQuery("SELECT count(*) FROM Books");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            new OtherTransaction().start();
            Thread.sleep(2000);

            ResultSet resultSet2 = stat.executeQuery("SELECT count(*) FROM Books");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getInt(1));
            }

        }
    }

    static class OtherTransaction extends Thread {
        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
                 Statement stat = conn.createStatement()) {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                stat.executeUpdate("insert into Books (name) VALUES ('new value')");

                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}























