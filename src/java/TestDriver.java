package miniproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TestDriver {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484", "sql9657484", "e8X5f44Fl9");
            runSimulation(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void runSimulation(Connection connection) {
        // Create instances of existing classes
        BusRoute[] routes = createSampleRoutes(connection);

        // Set up a timer to simulate updates
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Simulate route progression and new passengers
                updateRoutes(routes, connection);
            }
        }, 0, 30000); // Update every 30 seconds
    }

    private static BusRoute[] createSampleRoutes(Connection connection) {
        BusRoute[] routes = new BusRoute[9]; // Total of 9 routes

        // Create and add routes to the array
        for (int i = 0; i < 9; i++) {
            Driver driver = new Driver(i + 1, "Driver " + (i + 1), 50000.0);
            Bus bus = new Bus(i + 1, driver);

            // Assuming there are 3 stops for each route
            ArrayList<Stop> stops = new ArrayList<>();
            stops.add(new Stop(i * 3 + 1, "Stop " + (i * 3 + 1), 1200));
            stops.add(new Stop(i * 3 + 2, "Stop " + (i * 3 + 2), 1200));
            stops.add(new Stop(i * 3 + 3, "Stop " + (i * 3 + 3), 1200));

            Route route = new Route(i + 1, "Route " + (i + 1), stops);
            BusRoute busRoute = new BusRoute(i + 1, bus, route);
            routes[i] = busRoute;
        }

        return routes;
    }

    private static void updateRoutes(BusRoute[] routes, Connection connection) {
        for (BusRoute route : routes) {
            // Update the current stop to the next stop
            int nextStopIndex = (route.getCurrentStopIndex() + 1) % route.getRoute().getStops().size();
            route.setCurrentStopId(nextStopIndex);

            // Adding at least one new passenger at every new stop
            Passenger newPassenger = createNewPassenger(route, connection);
            route.addPassenger(newPassenger);
        }
    }

    private static Passenger createNewPassenger(BusRoute route, Connection connection) {
        // New passenger is created with a boarded stop being the current stop and a random payment method
        int passengerId = 0; // Assign a unique passenger ID based on your logic
        Stop boardedStop = route.getCurrentStop();
        Stop departedStop = null; // Passenger has not departed yet
        PaymentMethod paymentMethod = getRandomPaymentMethod(connection);

        return new Passenger(passengerId, boardedStop, departedStop, paymentMethod);
    }

    private static PaymentMethod getRandomPaymentMethod(Connection connection) {
        try {
            // Query to retrieve a random payment method from the database
            String query = "SELECT * FROM payment_method ORDER BY RAND() LIMIT 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    int methodId = resultSet.getInt("method_id");
                    String methodName = resultSet.getString("method_name");
                    double methodPrice = resultSet.getDouble("method_price");

                    // Create and return a PaymentMethod object
                    return new PaymentMethod(methodId, methodName, methodPrice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return a default PaymentMethod if an error occurs or no records are found
        return new PaymentMethod(0, "Default Method", 0.0);
    }
}
