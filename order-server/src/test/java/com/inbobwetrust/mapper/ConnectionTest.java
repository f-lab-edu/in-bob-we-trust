package com.inbobwetrust.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionTest {

    @Test
    @DisplayName("DB 연결 테스트")
    void dbConnectionTest() {
        try (Connection con =
                DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/inbobwetrust?characterEncoding=UTF-8&serverTimezone=UTC",
                        "root",
                        "1qaz@WSX")) {
            System.out.println("Connection Success! : " + con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
