package miniproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bus {
    private int id;
    private Driver driver;
 
    public Bus(int id, Driver driver) {
        this.id = id;
        this.driver = driver;
    }
    
    public Bus(Driver driver, Connection connection) {
        String query = "SELECT * FROM bus WHERE driver_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, driver.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                this.id = resultSet.getInt("bus_id");
                this.driver = driver;
            } else {
                // Handle the case where no data is retrieved for the given driver's ID
                System.out.println("Bus for Driver ID " + driver.getId() + " not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
    }
 
    public int getId() {
        return id;
    }
 
    public Driver getDriver() {
        return driver;
    }
}
