package com.inbobwetrust.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;

@ActiveProfiles("test")
@SpringBootTest
public class TestProfileH2DatabaseConnectionTest {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Test
    @DisplayName("테스트용 h2-인메모리 DB 연결 테스트")
    void dbConnectionTest() {
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connection Success! : " + con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
