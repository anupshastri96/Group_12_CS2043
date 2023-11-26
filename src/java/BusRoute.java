package miniproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javafx.scene.paint.Stop;

public class BusRoute {

    private int id;
    private int currentStopIndex;
    private Bus bus;
    private Route route;
    private Timestamp departureTime;
    private ArrayList<Passenger> passengers;

    private final String LATE_STATUS = "Late";
    private final String EARLY_STATUS = "Early";
    private final String ON_TIME_STATUS = "On Time";
    private final int LATE_BUFFER = 3;
    private final int EARLY_BUFFER = -3;

    public BusRoute(int id, Bus bus, Route route) {
        this.id = id;
        this.bus = bus;
        this.route = route;
        this.passengers = new ArrayList<>(); // Initialize the passenger list
    }

	public BusRoute(int ID, Bus bus, Route route, Timestamp departureTime, Connection connection) {
        this.id = ID;
        this.bus = bus;
        this.route = route;
        this.passengers = new ArrayList<>(); // Initialize the passenger list

        // Push the variables to the database
        String insertQuery = "INSERT INTO bus_route (bus_id, route_id) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertStatement.setInt(1, bus.getId());
            insertStatement.setInt(2, route.getId());

            int rowsAffected = insertStatement.executeUpdate();
            if (rowsAffected <= 0) {
                throw new SQLException("Failed to insert bus route into the database.");
                }
        }
		catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
	}


 // Database constructor
    public BusRoute(int busRouteId, Connection connection) {
        // Retrieve bus route information
        String routeQuery = "SELECT * FROM bus_route WHERE bus_route_id = ?";
        try (PreparedStatement routeStatement = connection.prepareStatement(routeQuery)) {
            routeStatement.setInt(1, busRouteId);
            ResultSet routeResultSet = routeStatement.executeQuery();

            if (routeResultSet.next()) {
                // Extract bus route information from the result set
                this.id = routeResultSet.getInt("bus_route_id");
                int routeId = routeResultSet.getInt("route_id");

                // Create and set the Route object using another method to retrieve route details
                this.route = new Route(routeId, connection);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }

        // Retrieve passengers associated with the bus route
        String passengersQuery = "SELECT p.* " +
                "FROM passenger p " +
                "JOIN bus_route_passenger brp ON p.passenger_id = brp.passenger_id " +
                "WHERE brp.bus_route_id = ?";

        try (PreparedStatement passengersStatement = connection.prepareStatement(passengersQuery)) {
            passengersStatement.setInt(1, busRouteId);
            ResultSet passengersResultSet = passengersStatement.executeQuery();

            ArrayList<Passenger> passengersList = new ArrayList<>();
            while (passengersResultSet.next()) {
                int passengerId = passengersResultSet.getInt("passenger_id");

                // Create Passenger objects based on the available constructors
                Passenger passenger = new Passenger(passengerId, connection);

                passengersList.add(passenger);
            }

            this.passengers = passengersList;

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception 
        }
    }
	public int getId() {
		return id;
	}
	
	public Bus getBus() {
		return bus;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Stop getCurrentStop() {
		return route.getStops().get(currentStopIndex);
	}
	
	public void setCurrentStopId(int stopIndex) throws IllegalArgumentException {
		if (id >= route.getStops().size()) {
			throw new IllegalArgumentException("Stop ID outside range of Stops on this Route");
		} else {
			currentStopIndex = stopIndex;
		}
	}
	
	public Timestamp getDepartureTime() {
		return departureTime;
	}
	
	public void setDepartureTime(Timestamp departureTime) {
		this.departureTime = departureTime;
	}
	
	public ArrayList<Passenger> getPassengers() {
		return passengers;
	}
	
	public void addPassenger(Passenger p) {
		passengers.add(p);
	}

	public void addPassenger(Passenger passenger, Connection connection) {
        // Add the passenger to the local list
        passengers.add(passenger);

        // Add the passenger to the database
        String addPassengerQuery = "INSERT INTO bus_route_passenger (bus_route_id, passenger_id) VALUES (?, ?)";
        try (PreparedStatement addPassengerStatement = connection.prepareStatement(addPassengerQuery)) {
            addPassengerStatement.setInt(1, this.id);
            addPassengerStatement.setInt(2, passenger.getId());

            int rowsAffected = addPassengerStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Passenger added to the database successfully.");
            } else {
                System.out.println("Failed to add passenger to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
	
	public String getOnTimeStatus() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		
		if(isLate(currentTime)) {
			return LATE_STATUS;
		} else if(isEarly(currentTime)) {
			return EARLY_STATUS;
		} else {
			return ON_TIME_STATUS;
		}
	}
	
	private boolean isLate(Timestamp currentTime) {
		if (currentStopIndex + 1 < route.getStops().size()) {
			Timestamp lateTime = new Timestamp(departureTime.getTime());
			Stop nextStop = route.getStops().get(currentStopIndex + 1);
			lateTime.setMinutes(lateTime.getMinutes() + nextStop.getExpectedArrivalTime() + LATE_BUFFER);
			return currentTime.compareTo(lateTime) > 0;
		} else {
			return false;
		}
	}
	
	private boolean isEarly(Timestamp currentTime) {
		Timestamp earlyTime = new Timestamp(departureTime.getTime());
		earlyTime.setMinutes(departureTime.getMinutes() + getCurrentStop().getExpectedArrivalTime() + EARLY_BUFFER);
		return currentTime.compareTo(earlyTime) < 0;
	}
}
