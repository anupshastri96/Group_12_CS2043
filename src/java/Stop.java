package miniproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Stop {
    private int id;
    private String location;
    private int expectedArrivalTime;
 
    public Stop(int id, String location, int expectedArrivalTime) {
        this.id = id;
        this.location = location;
        this.expectedArrivalTime = expectedArrivalTime;
        
    }
    
 // Constructor that accesses the database directly
    public Stop(Connection connection, int stopId) { //Not sure how to be handling the expected arrival times -Rusty
        String query = "SELECT stop_location, arrival_time FROM stop WHERE stop_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, stopId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.id = stopId;
                    this.location = resultSet.getString("stop_location");
                    this.expectedArrivalTime = resultSet.getInt("arrival_time");
                } else {
                    // Handle the case where no data is found for the given stopId
                    System.out.println("Stop with ID " + stopId + " not found in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    public Stop(Connection connection, int stopId, String location, int expectedArrivalTime) {
        // Add the stop to the local object
        this.id = stopId;
        this.location = location;
        this.expectedArrivalTime = expectedArrivalTime;

        // Add the stop to the database
        String addStopQuery = "INSERT INTO stop (stop_id, stop_location, arrival_time) VALUES (?, ?, ?)";
        try (PreparedStatement addStopStatement = connection.prepareStatement(addStopQuery)) {
            addStopStatement.setInt(1, stopId);
            addStopStatement.setString(2, location);
            addStopStatement.setInt(3, expectedArrivalTime);

            int rowsAffected = addStopStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Stop added to the database successfully.");
            } else {
                System.out.println("Failed to add stop to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
 
    public int getId() {
        return id;
    }
 
    public String getLocation() {
        return location;
    }
 
    public int getExpectedArrivalTime() {
        return expectedArrivalTime;
    }
 
    public void setExpectedArrivalTime(int newExpectedArrivalTime) {
        this.expectedArrivalTime = newExpectedArrivalTime;
    }
}
