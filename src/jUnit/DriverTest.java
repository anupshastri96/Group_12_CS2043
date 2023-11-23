package miniproject;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DriverTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() {
        // Set up the database connection before running the tests
        try {
            // Replace the placeholders with your actual database details
            String jdbcUrl = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
            String username = "sql9657484";
            String password = "e8X5f44Fl9";

            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    @AfterAll
    static void tearDown() {
        // Close the database connection after running all tests
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log or handle the exception appropriately
            e.printStackTrace();
        }
    }

    @Test
    void testGetId() {
        Driver driver = new Driver(1, "John Doe", 50000);
        assertEquals(1, driver.getId());
    }

    @Test
    void testGetName() {
        Driver driver = new Driver(1, "John Doe", 50000);
        assertEquals("John Doe", driver.getName());
    }

    @Test
    void testGetSalary() {
        Driver driver = new Driver(1, "John Doe", 50000);
        assertEquals(50000, driver.getSalary());
    }

    @Test
    void testSetSalary() {
        Driver driver = new Driver(1, "John Doe", 50000);
        driver.setSalary(60000);
        assertEquals(60000, driver.getSalary());
    }

    @Test
    void testSetSalaryWithDatabase() {
        // Assuming there is a driver with ID 1 in the database
        Driver driver = new Driver(1, connection);

        double newSalary = 70000;
        driver.setSalary(newSalary, connection);

        // Verify that the salary is updated in the object and in the database
        assertEquals(newSalary, driver.getSalary());

        // You may also add additional assertions to check the database state
    }
}
