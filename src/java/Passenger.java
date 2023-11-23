package miniproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Passenger {
    private int id;
    private Stop boardedStop;
    private Stop departedStop;
    private PaymentMethod paymentMethod;
 
    public Passenger(int id, Stop boardedStop, Stop departedStop, PaymentMethod paymentMethod) {
        this.id = id;
        this.boardedStop = boardedStop;
        this.departedStop = departedStop;
        this.paymentMethod = paymentMethod;
    }
    
    
    public Passenger(int passengerId, Connection connection) {
        // Retrieve passenger information
        String passengerQuery = "SELECT * FROM passenger WHERE passenger_id = ?";
        try (PreparedStatement passengerStatement = connection.prepareStatement(passengerQuery)) {
            passengerStatement.setInt(1, passengerId);
            ResultSet passengerResultSet = passengerStatement.executeQuery();

            if (passengerResultSet.next()) {
                // Extract passenger information from the result set
                this.id = passengerResultSet.getInt("passenger_id");
                int boardedStopId = passengerResultSet.getInt("boarded_stop");
                int departedStopId = passengerResultSet.getInt("departed_stop");
                // Retrieve stops from the database based on stop IDs
                this.boardedStop = retrieveStop(boardedStopId, connection);
                this.departedStop = retrieveStop(departedStopId, connection);
                // Retrieve payment method information from the database
                int paymentMethodId = passengerResultSet.getInt("payment_method");
                this.paymentMethod = retrievePaymentMethod(paymentMethodId, connection);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }
    
    //Utilities for creating objects

    private Stop retrieveStop(int stopId, Connection connection) throws SQLException {
        String stopQuery = "SELECT * FROM stop WHERE stop_id = ?";
        try (PreparedStatement stopStatement = connection.prepareStatement(stopQuery)) {
            stopStatement.setInt(1, stopId);
            ResultSet stopResultSet = stopStatement.executeQuery();

            if (stopResultSet.next()) {
                // Extract stop information from the result set
                // Create and return a Stop object
                int id = stopResultSet.getInt("stop_id");
                String location = stopResultSet.getString("stop_location");
                int expectedArrivalTime = 1200;
                return new Stop(id, location, expectedArrivalTime);
            }
        }

        return null;
    }

    private PaymentMethod retrievePaymentMethod(int paymentMethodId, Connection connection) throws SQLException {
        String paymentMethodQuery = "SELECT * FROM payment_method WHERE method_id = ?";
        try (PreparedStatement paymentMethodStatement = connection.prepareStatement(paymentMethodQuery)) {
            paymentMethodStatement.setInt(1, paymentMethodId);
            ResultSet paymentMethodResultSet = paymentMethodStatement.executeQuery();

            if (paymentMethodResultSet.next()) {
                // Extract payment method information from the result set
                // Create and return a PaymentMethod object
                int id = paymentMethodResultSet.getInt("method_id");
                String type = paymentMethodResultSet.getString("method_name");
                double price = paymentMethodResultSet.getDouble("method_price");
                return new PaymentMethod(id, type, price);
            }
        }

        return null;
    }
    
 
    public int getId() {
        return id;
    }
 
    public Stop getBoardedStop() {
        return boardedStop;
    }
 
    public void setBoardedStop(Stop newBoardedStop) {
        this.boardedStop = newBoardedStop;
    }
 
    public Stop getDepartedStop() {
        return departedStop;
    }
 
    public void setDepartedStop(Stop newDepartedStop) {
        this.departedStop = newDepartedStop;
    }
    public void setBoardedStop(Stop newBoardedStop, Connection connection) {
        this.boardedStop = newBoardedStop;
        String query = "UPDATE passenger SET boarded_stop = ? WHERE passenger_id = ?";
        updateStopInDatabase(connection, query, boardedStop);
    }

    public void setDepartedStop(Stop newDepartedStop, Connection connection) {
        this.departedStop = newDepartedStop;
        String query = "UPDATE passenger SET departed_stop = ? WHERE passenger_id = ?";
        updateStopInDatabase(connection, query, departedStop);
    }

    private void updateStopInDatabase(Connection connection, String query, Stop stop) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, stop != null ? stop.getId() : 0);
            preparedStatement.setInt(2, this.id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Stop updated successfully.");
            } else {
                System.out.println("Failed to update stop in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }
 
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
