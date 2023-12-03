package src.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.Statement;
import java.util.Random;


public class TestDriver {
	
	private static final String URL = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
    private static final String USER = "sql9657484";
    private static final String PASSWORD = "e8X5f44Fl9";
    private static final int UPDATE_INTERVAL_SECONDS = 30;
    private static int passengers = 0;

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
			}, 10, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
        } catch (SQLException e) {
            e.printStackTrace();
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
            Bus bus = new Bus(driver,i + 1,  connection);

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
        for (BusRoute route : routes) {
        	 int random = new Random().nextInt(10);
        	
        	 if (random % 2 > 0) {
            int nextStopIndex = (route.getCurrentStopIndex() + 1);//Someone needs to fix this pretty please (I'm going mentally insane :)
            route.setCurrentStopIndex(connection, nextStopIndex);

            // Adding at least one new passenger at every new stop
            Passenger newPassenger = createNewPassenger(route, connection);
            route.addPassenger(newPassenger);
            passengers++;
        	 }
        }
    }

    // Apparently this isn't actually adding passengers properly - this needs to be fixed
    
    private static Passenger createNewPassenger(BusRoute route, Connection connection) {
        //New passenger is created with a boarded stop being the current stop and a random payment method
        int passengerId = passengers; // Assign a unique passenger ID based on your logic
        Stop boardedStop = route.getCurrentStop();
        Stop departedStop = route.getCurrentStop(); // Passenger has not departed yet
        PaymentMethod paymentMethod = getRandomPaymentMethod(connection);

        return new Passenger(passengerId, boardedStop,route, departedStop, paymentMethod, connection);
    }

    private static PaymentMethod getRandomPaymentMethod(Connection connection) {
        //Add our db payment menthod retrival logic here 
        int randomMethodId = new Random().nextInt(3) +1 ; // Replace with our logic to get a random payment method ID
        return new PaymentMethod(connection, randomMethodId);
    }
    
    // We need a cleanup method to remove everything we added to the database at the very end of running
}
