package com.furniture;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {
            // should change the name of data base
            String url = "jdbc:mysql://localhost:3306/FurnitureShowroomManagementSystem";
            String user = "root";
            String password = "1234";

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected Successfully!");

            return conn;

        } catch (Exception e) {

            System.out.println("Connection Failed!");
            e.printStackTrace();

            return null;
        }
    }
}