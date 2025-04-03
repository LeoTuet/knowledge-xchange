package de.leotuet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/knowledge-xchange";
        String user = "root";
        String password = "admin";

        String query = "SELECT * FROM Kunden";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            // Daten ausgeben
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("K_ID"));
                System.out.println("Name: " + rs.getString("K_Name"));
                System.out.println("----------------------");
            }

            query = "SELECT * FROM Kunden WHERE K_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, "Peterson");
            ResultSet rs1 = pstmt.executeQuery();

            while (rs1.next()) {
                System.out.println("ID: " + rs1.getInt("K_ID"));
                System.out.println("Name: " + rs1.getString("K_Name") + rs1.getString("K_Vorname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

}