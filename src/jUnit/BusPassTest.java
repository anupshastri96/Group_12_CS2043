package jUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import classes.BusPass;

class BusPassTest {

    @Test
    void testBusPassConstructor() {
        int id = 1;
        String type = "Monthly Pass";
        double price = 50.0;

        BusPass busPass = new BusPass(id, type, price);

        assertNotNull(busPass);
        assertEquals(id, busPass.getId());
        assertEquals(type, busPass.getType());
        assertEquals(price, busPass.getPrice());
    }

}
