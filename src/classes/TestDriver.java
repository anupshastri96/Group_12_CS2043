package src.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.Statement;
import java.sql.ResultSet;


public class TestDriver {
	
	private static final String URL = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
    private static final String USER = "sql9657484";
    private static final String PASSWORD = "e8X5f44Fl9";
    private static final int UPDATE_INTERVAL_SECONDS = 30;

	// Declare routeToUpdate as a class-level variable
    private static int routeToUpdate = 0;

    public static void main(String[] args) {
        // Connect to the database
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
        	 BusRoute[] routes = createSampleRoutes(connection);
        	 ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
             executorService.scheduleAtFixedRate(() -> {
				try {
					updateRoutes(routes, DriverManager.getConnection(URL, USER, PASSWORD));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}, 5, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Clean up data at the end
            cleanupData();
     }
    }
	
    private static void deleteFromRouteStop(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM `sql9657484`.`route_stop` WHERE stop_id < 28";
            statement.executeUpdate(sql);
            System.out.println("Rows deleted from route_stop table.");
        }
    }

    private static void deleteFromStop(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM `sql9657484`.`stop` WHERE stop_id < 28";
            statement.executeUpdate(sql);
            System.out.println("Rows deleted from stop table.");
        }
    }

    private static BusRoute[] createSampleRoutes(Connection connection) {
        BusRoute[] routes = new BusRoute[9]; //Total of 9 routes 

        // Create and add routes to the array
        for (int i = 0; i < 9; i++) {
            Driver driver = new Driver( i + 1, "Driver " + (i + 1), 50000.0, connection);
            Bus bus = new Bus(i + 1, driver);

            // Assuming there are 3 stops for each route
            ArrayList<Stop> stops = new ArrayList<>();
            stops.add(new Stop(connection, i * 3 + 1, "Stop " + (i * 3 + 1), 1200));
            stops.add(new Stop(connection, i * 3 + 2, "Stop " + (i * 3 + 2), 1200));
            stops.add(new Stop(connection, i * 3 + 3, "Stop " + (i * 3 + 3), 1200));

            Route route = new Route(connection, i, "Route " + (i), stops);
            BusRoute busRoute = new BusRoute(i, bus, route, connection);
            routes[i] = busRoute;
            System.out.println(routes[i].getRoute().getStops().size());
        }

        return routes;
    }

    // These updates should be done in a staggered manner - i.e. Route 1 updates to the next stop, then Route 2 at the next timer, etc. for more realistic live updating
    // Refer to the Nov 23rd notes doc in the D5 folder on Teams for one way you could do this
    
     private static void updateRoutes(BusRoute[] routes, Connection connection) {
        BusRoute route = routes[routeToUpdate];

        // Update the current stop to the next stop if within the bounds
        int nextStopIndex = route.getCurrentStopIndex() + 1;
        if (nextStopIndex < route.getRoute().getStops().size()) {
            route.setCurrentStopIndex(connection, nextStopIndex);

            // Adding random passengers at every new stop
            int numberOfPassengers = (int) (Math.random() * 10) + 1;
            for (int i = 0; i < numberOfPassengers; i++) {
                Passenger newPassenger = createNewPassenger(route, connection, i);
                route.addPassenger(newPassenger);
            }
        }

        // Update the route variable for the next iteration
        routeToUpdate = (routeToUpdate + 1) % routes.length;
    }

    // Apparently this isn't actually adding passengers properly - this needs to be fixed
    
    private static Passenger createNewPassenger(BusRoute route, Connection connection, int i) {
        //New passenger is created with a boarded stop being the current stop and a random payment method
        int passengerId = i; // Assign a unique passenger ID based on your logic
        Stop boardedStop = route.getCurrentStop();
        Stop departedStop = null; // Passenger has not departed yet
        PaymentMethod paymentMethod = getRandomPaymentMethod(connection);

        return new Passenger(passengerId, boardedStop, departedStop, paymentMethod);
    }

    private static PaymentMethod getRandomPaymentMethod(Connection connection) {
        //Add our db payment method retrival logic here 
        String query = "select method_id from payment_method order by RAND() LIMIT 1"; // Query that returns a method ID

        try( PreparedStatement preparedStatement = connection.prepareStatement(query);
            
            ResultSet resultSet = preparedStatement.executeQuery()){ // Stores the result into the resultSet type object
            if(resultSet.next()){
                int methodID= resultSet.getInt("method_id");
                return new PaymentMethod(connection, methodID);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    // We need a cleanup method to remove everything we added to the database at the very end of running
       private static void cleanupData() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            deleteFromRouteStop(connection);
            deleteFromStop(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

