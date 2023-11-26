package miniproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMethod {
    private int id;
    private String type;
    private double price;
 
    public PaymentMethod(int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }
 
    public PaymentMethod(Connection connection, int method_id) {
        String query = "SELECT method_name, method_price FROM payment_method WHERE method_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, method_id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.id = method_id;
                    this.type = resultSet.getString("method_name");
                    this.price = resultSet.getDouble("method_price");
                } else {
                    // Handle the case where no data is found for the given paymentMethodId
                    System.out.println("PaymentMethod with ID " + method_id + " not found in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
    }

    public PaymentMethod(Connection connection, int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;

        // Push the PaymentMethod variables to the database
        String updatePaymentMethodQuery = "INSERT INTO payment_method (method_id, method_name, method_price) VALUES (?, ?, ?)";
        try (PreparedStatement updatePaymentMethodStatement = connection.prepareStatement(updatePaymentMethodQuery)) {
            updatePaymentMethodStatement.setInt(1, id);
            updatePaymentMethodStatement.setString(2, type);
            updatePaymentMethodStatement.setDouble(3, price);

            int rowsAffected = updatePaymentMethodStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PaymentMethod added successfully to the database.");
            } else {
                System.out.println("Failed to insert PaymentMethod into the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
    
    public int getId() {
        return id;
    }
 
    public String getType() {
        return type;
    }
 
    public double getPrice() {
        return price;
    }
 
    public void setPrice(double newPrice) {
        this.price = newPrice;
    }
    
    
    public void setPrice(double newPrice, Connection connection) {
        this.price = newPrice;

        // Update the price in the database
        String updateQuery = "UPDATE payment_method SET method_price = ? WHERE method_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, newPrice);
            preparedStatement.setInt(2, this.id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Price updated successfully.");
            } else {
                System.out.println("Failed to update price in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
    }

}
