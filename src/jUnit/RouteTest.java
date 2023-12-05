package jUnit;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.classes.Route;

class RouteTest {

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
    void testRouteIntConnection() {
        int validRouteId = 11;
        Route route = new Route(validRouteId, connection);

        // Perform assertions to check if the constructor populated the fields correctly
        assertEquals(validRouteId, route.getId());
        assertNotNull(route.getName());
        assertNotNull(route.getStops());
        assertFalse(route.getStops().isEmpty());
    }
}

