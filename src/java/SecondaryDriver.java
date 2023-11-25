package java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static org.junit.Assert.fail;

public class Secondary_Driver {

    public static void main(String[] args) {
        Connection connection = initializeDatabaseConnection();
        Timer timer = new Timer();

        List<BusRoute> busRoutes = initializeBusRoutes(connection);

        for (BusRoute busRoute : busRoutes) {
            int busRouteId = busRoute.getId();
            UpdateTask updateTask = new UpdateTask(busRouteId, connection);
            timer.schedule(updateTask, 0, 30000);
        }
    }

    private static Connection initializeDatabaseConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484", "sql9657484", "e8X5f44Fl9");
        } 
        catch (SQLException e) {
            e.printStackTrace();
            fail("cannot connect to DB");
        }
        return connection;
    }

    private static List<BusRoute> initializeBusRoutes(Connection connection) {
    List<BusRoute> busRoutes = new ArrayList<>();

    String query = "SELECT * FROM bus_route";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
            int busRouteId = resultSet.getInt("bus_route_id");
            BusRoute busRoute = new BusRoute(busRouteId, connection);
            busRoutes.add(busRoute); // add to the list
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return busRoutes;
}


    // UpdateTask class
    static class UpdateTask extends TimerTask {
        private int busRouteId;
        private Connection connection;

        public UpdateTask(int busRouteId, Connection connection) {
            this.busRouteId = busRouteId;
            this.connection = connection;
        }

        @Override
        public void run() {
            // Implement the logic to update the current stop ID for the specified bus route
            // You may need to handle cases where the current stop is the last stop on the route

            BusRoute busRoute = new BusRoute(busRouteId, connection);
            int currentStopIndex = busRoute.getCurrentStopIndex();

            // Logic to update the current stop ID (you may need to handle edge cases)
            int newStopIndex = currentStopIndex + 1;

            try {
                // Update the current stop ID in the database
                String updateQuery = "UPDATE bus_route SET current_stop_index = ? WHERE bus_route_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setInt(1, newStopIndex);
                    preparedStatement.setInt(2, busRouteId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Current stop index updated successfully for Bus Route " + busRouteId);
                    } else {
                        System.out.println("Failed to update current stop index for Bus Route " + busRouteId);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

