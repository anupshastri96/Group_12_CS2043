package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Route {
    private int id;
    private String name;
    private ArrayList<Stop> stops;

    public Route(int id, String name, ArrayList<Stop> stops) {
        this.id = id;
        this.name = name;
        this.stops = stops;
    }

    // Database constructor
    public Route(int routeId, Connection connection) {
        String routeQuery = "SELECT * FROM route WHERE route_id = ?";
        String stopsQuery = "SELECT s.* FROM stop s JOIN route_stop rs ON s.stop_id = rs.stop_id WHERE rs.route_id = ?";

        try (PreparedStatement routeStatement = connection.prepareStatement(routeQuery);
             PreparedStatement stopsStatement = connection.prepareStatement(stopsQuery)) {

            // Retrieve route information
            routeStatement.setInt(1, routeId);
            ResultSet routeResultSet = routeStatement.executeQuery();

            if (routeResultSet.next()) {
                this.id = routeResultSet.getInt("route_id");
                this.name = routeResultSet.getString("route_name");
            } else {
                // Handle the case where no data is retrieved for the given route ID
                System.out.println("Route with ID " + routeId + " not found in the database.");
                return;
            }

            // Retrieve stops associated with the route
            stopsStatement.setInt(1, routeId);
            ResultSet stopsResultSet = stopsStatement.executeQuery();

            ArrayList<Stop> stopsList = new ArrayList<>();
            while (stopsResultSet.next()) {
                int stopId = stopsResultSet.getInt("stop_id");
                
                Stop stop = new Stop(connection,stopId );
                stopsList.add(stop);
            }

            this.stops = stopsList;

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }
}
