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

    public Route(Connection connection, int routeId, String routeName, ArrayList<Stop> stops) {
        // Add the route to the local object
        this.id = routeId;
        this.name = routeName;
        this.stops = stops;

        // Add the route to the database
        String addRouteQuery = "INSERT INTO route (route_id, route_name) VALUES (?, ?)";
        try (PreparedStatement addRouteStatement = connection.prepareStatement(addRouteQuery)) {
            addRouteStatement.setInt(1, routeId);
            addRouteStatement.setString(2, routeName);

            int rowsAffected = addRouteStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Route added to the database successfully.");
            } else {
                System.out.println("Failed to add route to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }

        // Add stops to the route_stop table
        for (Stop stop : stops) {
            String addStopToRouteQuery = "INSERT INTO route_stop (route_id, stop_id) VALUES (?, ?)";
            try (PreparedStatement addStopToRouteStatement = connection.prepareStatement(addStopToRouteQuery)) {
                addStopToRouteStatement.setInt(1, routeId);
                addStopToRouteStatement.setInt(2, stop.getId());

                int stopRowsAffected = addStopToRouteStatement.executeUpdate();

                if (stopRowsAffected > 0) {
                    System.out.println("Stop added to the route in the database successfully.");
                } else {
                    System.out.println("Failed to add stop to the route in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception
            }
        }
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
