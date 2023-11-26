package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

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
                this.departureTime = routeResultSet.getTimestamp("depart_time");
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
	
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Stop getCurrentStop() {
		return route.getStops().get(currentStopIndex);
	}
	
	public int getCurrentStopIndex() {
		return currentStopIndex;
	}
	
	public void setCurrentStopIndex(int stopIndex) throws IllegalArgumentException {
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
