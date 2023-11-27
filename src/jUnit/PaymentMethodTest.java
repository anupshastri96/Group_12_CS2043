package jUnit;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import classes.PaymentMethod;

class PaymentMethodTest {

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
    void testPaymentMethodConnectionInt() {
        int validMethodId = 1;
        PaymentMethod paymentMethod = new PaymentMethod(connection, validMethodId);

        assertEquals(validMethodId, paymentMethod.getId());
        assertNotNull(paymentMethod.getType());
        assertTrue(paymentMethod.getPrice() > 0);
    }

    @Test
    void testPaymentMethodInvalidId() {
        int invalidMethodId = -1;
        PaymentMethod paymentMethod = new PaymentMethod(connection, invalidMethodId);

        assertNull(paymentMethod.getType());
        assertEquals(0.0, paymentMethod.getPrice());
    }
}

