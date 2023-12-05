package src.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Driver {
    private int id;
    private String name;
    private double salary;

    public Driver(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public Driver(int id, String name, double salary, Connection connection) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        // Push the driver variables to the database
        String updateDriverQuery = "INSERT INTO driver (driver_id, driver_name, driver_salary) VALUES (?, ?, ?)";
        try (PreparedStatement updateDriverStatement = connection.prepareStatement(updateDriverQuery)) {
            updateDriverStatement.setInt(1, id);
            updateDriverStatement.setString(2, name);
            updateDriverStatement.setDouble(3, salary);

            int rowsAffected = updateDriverStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Driver added successfully to the database.");
            } else {
                System.out.println("Failed to insert driver into the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    // Database constructor
    public Driver(int driverId, Connection connection) {
        String query = "SELECT * FROM driver WHERE driver_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, driverId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                this.id = resultSet.getInt("driver_id");
                this.name = resultSet.getString("driver_name");
                this.salary = resultSet.getDouble("driver_salary");
            } else {
                // Handle the case where no data is retrieved for the given driverId
                System.out.println("Driver with ID " + driverId + " not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double newSalary) {
        this.salary = newSalary;
    }
    
    public void setSalary(double newSalary, Connection connection) {
        this.salary = newSalary; // Update the salary in the object

        String query = "UPDATE driver SET driver_salary = ? WHERE driver_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, newSalary);
            preparedStatement.setInt(2, this.id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Salary updated successfully in the database.");
            } else {
                System.out.println("Failed to update salary in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
}
