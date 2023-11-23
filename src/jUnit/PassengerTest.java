package miniproject;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PassengerTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484", "sql9657484", "e8X5f44Fl9");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPassengerIntConnection() {
        int validPassengerId = 1;
        Passenger passenger = new Passenger(validPassengerId, connection);
        assertEquals(validPassengerId, passenger.getId());
        assertNotNull(passenger.getBoardedStop());
        assertNotNull(passenger.getDepartedStop());
        assertNotNull(passenger.getPaymentMethod());
    }
}

