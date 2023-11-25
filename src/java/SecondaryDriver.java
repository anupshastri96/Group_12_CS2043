package java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static org.junit.Assert.fail;

public class SecondaryDriver {

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
    static class UpdateTask extends TimerTask {
        private int busRouteId;
        private Connection connection;

        public UpdateTask(int busRouteId, Connection connection) {
            this.busRouteId = busRouteId;
            this.connection = connection;
        }

        @Override
        public void run() {
            BusRoute busRoute = new BusRoute(busRouteId, connection);

            // Implement the logic for cyclic traversal of stops
            int currentStopIndex = busRoute.getCurrentStopIndex();

            // Retrieve the stops for the route from the database
            String stopsQuery = "SELECT * FROM stops WHERE route_id = ? ORDER BY stop_index";
            try (PreparedStatement stopsStatement = connection.prepareStatement(stopsQuery)) {
                stopsStatement.setInt(1, busRoute.getRoute().getId());
                ResultSet stopsResultSet = stopsStatement.executeQuery();

                int lastStopIndex = -1;
                while (stopsResultSet.next()) {
                    lastStopIndex = stopsResultSet.getInt("stop_index");
                }

                if (currentStopIndex < lastStopIndex) {
                    // Increment the stop index
                    int newStopIndex = currentStopIndex + 1;

                    // Update the current stop index in the database
                    updateCurrentStopIndex(newStopIndex);

                    System.out.println("Current stop index updated successfully for Bus Route " + busRouteId);
                } else {
                    // Reset to the first stop (cyclic traversal)
                    int newStopIndex = 0;

                    // Update the current stop index in the database
                    updateCurrentStopIndex(newStopIndex);

                    System.out.println("Reached the end of stops. Resetting to the first stop for Bus Route " + busRouteId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void updateCurrentStopIndex(int newStopIndex) throws SQLException {
            String updateQuery = "UPDATE bus_route SET current_stop_index = ? WHERE bus_route_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, newStopIndex);
                preparedStatement.setInt(2, busRouteId);
                preparedStatement.executeUpdate();
            }
        }
    }
}

/*
 Database Connection: It establishes a connection to a MySQL database hosted at "sql9.freesqldatabase.com" on port 3306, using the username "sql9657484" and password "e8X5f44Fl9". The initializeDatabaseConnection method returns a Connection object.
Initialization of Bus Routes:

The initializeBusRoutes method retrieves data from the "bus_route" table in the database.
For each row in the result set, it creates a BusRoute object using the constructor that takes a busRouteId and a Connection as parameters.
The BusRoute objects are added to a list (busRoutes), which is returned.

UpdateTask Class:This is a nested class within SecondaryDriver that extends TimerTask.
It has a constructor that takes a busRouteId and a Connection.
The run method is where the logic for updating the current stop index for a bus route is implemented.
It creates a BusRoute object using the provided busRouteId and Connection.
It calculates the new stop index based on the current stop index and the total number of stops in the route.
It then performs an SQL update on the "bus_route" table to set the current stop index to the calculated value.
It prints success or failure messages based on the result of the update.

Main Method (in Secondary_Driver class): It initializes the database connection, creates a Timer object, and retrieves a list of bus routes using the initializeBusRoutes method.
For each bus route, it creates an UpdateTask object and schedules it to run every 30 seconds.
 */
