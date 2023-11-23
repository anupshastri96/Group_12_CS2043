package database_testing;
 
import static org.junit.jupiter.api.Assertions.*;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
 
class BusTest {
 
    private static Connection connection;
 
    @BeforeAll
    static void setUp() {
        // Set up the database connection before running the tests
        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484", "sql9657484", "e8X5f44Fl9");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to connect to the database");
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
            e.printStackTrace();
        }
    }
 
    @Test
    void testBusDriverConnection() {
        // Assuming you have a Driver class with a valid constructor and getId() method
        Driver validDriver = new Driver(101,"Ben Dover", 6969);
 
        // Test the Bus constructor with a valid Driver and Connection
        Bus bus = new Bus(validDriver, connection);
 
        // Perform assertions to check if the constructor populated the fields correctly
        assertNotNull(bus.getId());
        assertEquals(validDriver, bus.getDriver());
    }
}